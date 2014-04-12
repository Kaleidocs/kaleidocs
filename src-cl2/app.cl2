(load-file "angular-cl2/src/core.cl2")
(load-file "socket-cl2/src/client.cl2")
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
     (then (fn [res] (-> res :data :result))))))

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
                (success reload-table!))))
         ~@body)))

(deftabletype person
  ["fn" "ln" "em" "dc" "ph"]
  [{:foreign-type "template"
    :foreign-key "fn"}
   {:foreign-type "profile"
    :foreign-key "ln"}]
  (console.log "Welcome to peron's hell"))

(deftabletype document
  [:filename :fields]
  []
  )

(deftabletype docgroup
  [:name :documents]
  []
  )

(deftabletype profile
  [:company :bank :account :city]
  []
  )

(deftabletype contract
  [:records :date :sum]
  []
  )

(deftabletype record
  [:money :remarks :date :contract]
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
   (hiccup [:h3 "hue hue"])})

(deffilter field-type []
  [field-name]
  (if (contains? #{:sum :money} field-name)
    "number"
    "text"))
