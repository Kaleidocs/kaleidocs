(load-file "angular-cl2/src/core.cl2")
(load-file "socket-cl2/src/client.cl2")
(load-file "atom-crud/src/core.cl2")

(defapp my-app [ng-route angular-file-upload mgcrea.ng-strap
                ng-table ng-resource ng-sanitize ng-animate])

(.config my-app
 (fn-di
  [$modal-provider]
  ;; some problem with html unsafe
  ;; https://github.com/mgcrea/angular-strap/issues/369
  (. angular (extend (-> $modalProvider :defaults) {:html false}))))
#_
(defcontroller testbed-ctrl
  [$scope]
  (def$ modal
    {:content "Hello Modal<br />This is a multiline message! Have fun",
     :title "Title"}))

#_
(defcontroller testbed-ctrl
  [$scope $http]
  (def$ selected-address "")
  (defn$ get-address [view-value]
    (def params {:sensor false, :address view-value})
    (..
     $http
     (get
      "http://maps.googleapis.com/maps/api/geocode/json"
      {:params params})
     (then (fn [res] (-> res :data :results))))))

(defmacro deftabletype [entity-type fixed-fields & body]
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

(deftabletype person ["fn" "ln" "em" "dc" "ph"]
  (console.log "Welcome to peron's hell"))

(defroute
  "/person"
  {:controller 'person-ctrl
   :template
   (hiccup
    [:div {:ng-include "'edit-row.html'"
           :ng-controller "createPersonCtrl"}]
    [:div {:ng-include "'table.html'"}]
    #_
    [:input.form-control
     {:bs-typeahead "",
      :placeholder "Enter address",
      :ng-options
      "address.formatted_address as address.formatted_address for address in getAddress($viewValue)",
      :data-animation "am-flip-x",
      :ng-model "selectedAddress",
      :type "text"}]
    #_
    [:form
     [:button.btn.btn-lg.btn-primary
      {:bs-modal "modal",
       :data-placement "center",
       :data-animation "am-fade-and-scale",
       :type "button"}
      "Click to toggle modal\n  "
      [:br]
      [:small "(using an object)"]]])}
  :default "/testbed")
