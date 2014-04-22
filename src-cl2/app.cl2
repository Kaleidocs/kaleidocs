(load-file "angular-cl2/src/core.cl2")
(load-file "atom-crud/src/core.cl2")

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

(defcontroller alerts-ctrl
  [$scope]
  ($<-atom alerts alerts vals)
  (defn$ clear-alerts []
    (reset! alerts {}))
  (defn$ remove-alert [id]
    (remove-entity! alerts id)))

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
           (defn on-success []
             (alert-msg "success" ~(str entity-type " added."))
             (reload-table!))
           (defn on-error [data status]
             (alert-msg "danger"
                        (str ~(str entity-type " not added. ")
                             "Error " status ": "
                             data))
             (reload-table!))
           (def$ p {})
           (defn$ save-item [item]
             (.. $http
                 (post ~query-url
                       item {"Content-Type" "application/json"})
                 (success on-success)
                 (error on-error))))

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
           (defn on-save-success []
             (alert-msg "success" ~(str entity-type " saved"))
             (reload-table!))
           (defn on-save-error []
             (alert-msg "danger" ~(str entity-type " not saved"))
             (reload-table!))
           (defn$ save-item [item]
             (.. $http
                 (post ~query-url
                       item {"Content-Type" "application/json"})
                 (success on-save-success)
                 (error on-save-error)))
           (defn on-delete-success []
             (alert-msg "success" ~(str entity-type " deleted"))
             (reload-table!))
           (defn on-delete-error []
             (alert-msg "danger" ~(str entity-type " not deleted"))
             (reload-table!))
           (defn$ delete-item [id]
             (. ($http {:method "DELETE" :url (str ~query-url "/" id)})
                (success on-delete-success)
                (error on-delete-error)))
           (defn$ generate-item [id]
             (let [alert-id (next-alert-id)
                   on-generate-success
                   (fn []
                     (add-entity!
                      alerts nil
                      {:id alert-id
                       :type "success"
                       :msg (str "Finished " ~(name entity-type)
                                 " #" id "...")})
                     (reload-table!))
                   on-generate-error
                   (fn []
                     (add-entity!
                      alerts nil
                      {:id alert-id
                       :type "danger"
                       :msg (str "Error generating " ~(name entity-type)
                                 " #" id "...")})
                     (reload-table!))]
               (add-entity!
                alerts nil
                {:id alert-id
                 :type "info"
                 :msg (str "Generating " ~(name entity-type)
                           " #" id "...")})
               (..
                $http
                (get
                 ~(str "/generate/" (name entity-type))
                 {:params {:id id}})
                (success on-generate-success)
                (error on-generate-error))))
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
   (contains? #{:id :account :sum :date :money} key)
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

(def alerts (atom {}))
(defn next-alert-id []
  (inc (let [ids (keys @alerts)]
         (if (= [] ids)
           0
           (apply max ids)))))

(defn alert-msg [type msg]
  (add-entity! alerts nil
               {:id (next-alert-id)
                :type type
                :msg msg}))
