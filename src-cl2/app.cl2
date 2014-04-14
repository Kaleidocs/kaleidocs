(load-file "angular-cl2/src/core.cl2")

(defapp my-app [ng-route angular-file-upload ui.bootstrap
                ng-table ng-resource ng-sanitize ng-animate])

(defn create-query-params [filter-key filter-value]
  (def params {:page 0 :count 6})
  (let [filter-key-string (str "filter[" filter-key "]")
        sort-key-string (str "sorting[" filter-key "]")]
    (set! (get params filter-key-string)
          filter-value)
    (set! (get params sort-key-string)
          "asc")
    params))

(defcontroller query-ctrl
  [$scope $http]
  (defn$ find-entity [entity-type filter-key filter-value]
    (..
     $http
     (get
      (str "/api/" entity-type)
      {:params (create-query-params filter-key filter-value)})
     (then (fn [res]
             (map #(add-details % entity-type filter-key)
                  (-> res :data :result)))))))

(defn string->set [s]
  (when (string? s)
    (->> ","
         (.split s)
         (map parse-int)
         (remove isNaN)
         set)))

(deffilter string->set []
  [s] (string->set s))

(defmacro deftabletype
  [entity-type fixed-fields foreign-fields & body]
  (let [ctrl-name (symbol (format "%s-ctrl" (name entity-type)))
        create-ctrl-name (symbol (format "create-%s-ctrl"
                                         (name entity-type)))
        query-url (str "/api/" entity-type)]
    `(do (defcontroller ~create-ctrl-name
           [$scope $http]
           (defn reload-table! []
             (def$ p {})
             (. ($- table-params) reload))
           (def$ p {})
           (defn$ save-item [item]
             (.. $http
                 (post ~query-url
                       item {"Content-Type" "application/json"})
                 (success reload-table!))))

         (defcontroller ~ctrl-name
           [$scope $filter ng-table-params $resource $timeout $http]
           (def$ entity-type ~(name entity-type))
           (def$ item-keys ~fixed-fields)
           (def$ foreign-keys ~foreign-fields)
           (def API ($resource ~query-url))
           (def$ filter-dict {})
           (defn reload-table! []
             (. ($- table-params) reload))
           (. $scope
              ($watch "filterDict"
                      (fn []
                        (if (and ($- filter-dict)
                                 ($- table-params))
                          (reload-table!)))
                      true))

           (def$ table-params
             ;; https://github.com/esvit/ng-table/wiki/Configuring-your-table-with-ngTableParams#parameters
             (ngTableParams.
              {:count 10, :page 1 :filter ($- filter-dict)}
              {:counts [] ;; disable "items per page" toggler
               :getData
               (fn [$defer params]
                 (.get API
                       (. params url)
                       (fn [data]
                         ($timeout
                          (fn []
                            (. params (total (:total data)))
                            (. $defer (resolve (:result data))))
                          500))))}))
           (def$ edit-id -1)
           (defn$ set-edit-id
             [pid]
             (def$ edit-id pid))
           (defn$ save-item [item]
             (.. $http
                 (post ~query-url
                       item {"Content-Type" "application/json"})
                 (success reload-table!)))
           (defn$ delete-item [id]
             (. ($http {:method "DELETE" :url (str ~query-url "/" id)})
                (success reload-table!)))
           (defn$ generate-item [id]
             (def$ status (str "Generating "  ~(name entity-type) " #" id "..."))
             (..
              $http
              (get
               ~(str "/generate/" (name entity-type))
               {:params {:id id}})
              (success (fn [data]
                         (def$ status
                           (str "Finished " ~(name entity-type) " #" id "..."))))))
           ~@body))))

(deftabletype document
  [:id :filename :fields]
  []
  )

(deftabletype docgroup
  [:id :name :documents]
  []
  )

(deftabletype profile
  [:id :company :bank :account :city]
  []
  )

(deftabletype contract
  [:id :records :date :sum]
  []
  (defn query-records [ids]
    (..
     $http
     (get
      "/records"
      {:params {:ids (.join (keys ids) ",")}})
     (success (fn [data]
                (def$ fetched-data data)))))
  (def last-queried-records #{})
  (defn$ watch-records [records]
    (when (not= last-queried-records records)
      (set! last-queried-records records)
      (query-records records)))
  nil)

(deftabletype record
  [:id :money :remarks :date]
  [{:foreign-type "docgroup"
    :foreign-key "name"}
   {:foreign-type "profile"
    :foreign-key "company"}]
  )

(defmacro with-tabletypes-routes
  [tabletype-symbols & other-routes]
  (let [tabletype-routes
        (mapcat
         #(let [type-name (name %)]
            [(str "/" type-name)
             {:controller (format "%sCtrl" type-name)
              :template
              `(hiccup
                [:div {:ng-include "'edit-row.html'"
                       :ng-controller
                       ~(format "create%sCtrl"
                               (clojure.string/capitalize type-name))}]
                [:div {:ng-include "'table.html'"}])}])
         tabletype-symbols)]
    `(defroute
       ~@tabletype-routes
       ~@other-routes)))

(with-tabletypes-routes
  [document docgroup profile contract record]
  "/testbed"
  {:controller 'query-ctrl
   :template
   (hiccup [:h3 "hue hue"])}
  :default
  "/record")

(deffilter field-type []
  [field-name]
  (if (contains? #{:sum :money} field-name)
    "number"
    (if (= :id field-name)
      "hidden"
      "text")))

(deffilter then-or-now [date-filter]
  [s]
  (or s
      (-> (Date.)
          .getTime
          (date-filter
           "dd/MM/yyyy"))))

(deffilter key-box-class []
  [key]
  (cond
   (contains? #{:id :account :sum :date} key)
   "text-right"
   (contains? #{} key)
   "text-justify"
   :default
   "text-left"))

(defn add-details
  [entity entity-type foreign-type]
  (let [head (str entity-type "#" (:id entity) " "
                  (get entity foreign-type))
        s (-> entity
              (dissoc foreign-type :id)
              vals
              (.join ", "))
        tail
        (when (not (= s ""))
          (str " (" s ")"))
        details
        (str head tail)]
    (assoc entity :_details details)))

(console.log "Yooloo" (map #(add-details % :bla :b)
                           [{:id 1 :b :foo :c :m}
                            {:id 2 :b :bar :c :n}
                            {:id 3 :b :boo :c :p}]))
