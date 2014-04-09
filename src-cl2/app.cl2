(load-file "angular-cl2/src/core.cl2")
(load-file "socket-cl2/src/client.cl2")
(load-file "atom-crud/src/core.cl2")

(defapp my-app [ng-route angular-file-upload mgcrea.ng-strap
                ng-table ng-resource])

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

(defcontroller testbed-ctrl
  [$scope $filter ng-table-params $resource $timeout]
  (def API ($resource "/testbed"))
  (def$ filter-dict {})
  (. $scope
     ($watch "filterDict"
             (fn []
               (if (and (-> $scope :filter-dict)
                        (-> $scope :table-params))
                 (.. (-> $scope :table-params) reload)))
             true))

  (def$ table-params
    (ngTableParams.
     {:count 10, :page 1}
     {:getData
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
    (def$ edit-id pid)))

(defroute
  "/testbed"
  {:controller 'testbed-ctrl
   :template
   (hiccup
    [:table.table
     {:show-filter "true", :ng-table "tableParams"}
     [:thead
      [:tr
       [:th.text-center.sortable
        {:ng-click
         "tableParams.sorting(key, tableParams.isSortBy(key, 'asc') ? 'desc' : 'asc')",
         :ng-class
         "{'sort-asc': tableParams.isSortBy(key, 'asc'),
           'sort-desc': tableParams.isSortBy(key, 'desc')}",
         :ng-repeat "key in ['id', 'fn', 'ln', 'em', 'dc', 'ph']"}
        "{{key}}"]]
      [:tr
       [:th.text-center.sortable
        {:ng-repeat "key in ['id', 'fn', 'ln', 'em', 'dc', 'ph']"}
        [:input.form-control
         {:ng-model "filterDict[key]",
          :type "text"}]]]]

     [:tbody
      {:ng-repeat "p in $data"}
      [:tr
       {:id "tr{{p.id}}"
        :ng-class-even "'even'", :ng-class-odd "'odd'"}
       [:td.rowTd
        {:ng-repeat "key in ['id', 'fn', 'ln', 'em', 'dc', 'ph']"
         ;;:filter "{ 'fn': 'text' }",
         :sortable "key",
         :data-title "key"}
        "{{p[key]}}"]
       [:td.rowTd
        [:input
         {:ng-click "setEditId(p.id)", :value "edit", :type "button"
          :id "editRowBtn{{p.id}}"}]]]
      [:tr
       {:ng-if "editId===p.id", :ng-show "editId===p.id"}
       [:td
        {:ng-include "'edit-row.html'",
         :colspan "7"}]]]]

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
