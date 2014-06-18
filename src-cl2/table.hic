[:table.table
 {:show-filter "true", :ng-table "tableParams"}
 [:thead
  [:tr
   [:th.text-center.sortable
    {:ng-click
     "tableParams.sorting(key, tableParams.isSortBy(key, 'asc') ? 'desc' : 'asc')",
     :ng-class
     "{'sort-asc':  tableParams.isSortBy(key, 'asc'),
       'sort-desc': tableParams.isSortBy(key, 'desc')}",
     :ng-repeat "key in itemKeys"}
    [:div "{{key}}"]]
   [:th.text-center.sortable
    {:ng-click
     "
tableParams.sorting(
  key.foreignType+'_id',
  tableParams.isSortBy(key.foreignType+'_id', 'asc') ? 'desc' : 'asc'
  )",
     :ng-class
     "{'sort-asc':  tableParams.isSortBy(key.foreignType+'_id', 'asc'),
       'sort-desc': tableParams.isSortBy(key.foreignType+'_id', 'desc')}",
     :ng-repeat "key in foreignKeys"}
    [:div "{{key.foreignType}}"]]
   [:th.text-center.sortable
    "Action"]]
  [:tr {:ng-init "q={}"}
   [:th.text-center.sortable
    {:ng-repeat "key in itemKeys"}
    [:input.form-control.input-sm
     {:ng-model "filterDict[key]",
      :type "text"}]]
   [:th.text-center.sortable
    {:ng-repeat "key in foreignKeys"}
    [:div {:ng-hide "true"}
     "{{ filterDict[key.foreignType+'_id'] = q[key.foreignType+'_full'].id }}"]
    [:input.form-control.input-sm
     {:name "{{key.foreignType}}",
      :ng-model "q[key.foreignType+'_full']"
      :ng-controller "queryCtrl"
      :typeahead "
entity
 as entity._details
 for entity in
  findEntity (key.foreignType, key.foreignKey, $viewValue)"}]]
   [:th.text-center.sortable]]]

 [:tbody
  {:ng-repeat "p in $data"}
  [:tr
   {:id "tr{{p.id}}"
    :ng-class-even "'even'", :ng-class-odd "'odd'"}
   [:td.rowTd
    {:ng-repeat "key in itemKeys"
     ;;:filter "{ 'fn': 'text' }",
     :sortable "key",
     :data-title "key"}
    [:p {:ng-class "key|keyBoxClass"} "{{p[key]|keyBoxFormat:key}}"]]
   [:td.rowTd
    {:ng-repeat "key in foreignKeys"
     ;;:filter "{ 'fn': 'text' }",
     :sortable "key",
     :data-title "key"}
    [:p {:ng-class "key.foreignKey|keyBoxClass"}
     "{{p[key.foreignKey]}}"]]
   [:td.rowTd
    [:button.btn.btn-xs.btn-info
     {:ng-if "(entityType=='record')||(entityType=='contract')"
      :ng-click "generateItem(p.id)", :type "button"}
     [:span.glyphicon.glyphicon-file] ""]
    [:button.btn.btn-xs.btn-warning
     {:ng-click "setEditId(p.id)", :type "button"
      :id "editRowBtn{{p.id}}"}
     [:span.glyphicon.glyphicon-pencil] ""]
    [:button.btn.btn-xs.btn-danger
     {:ng-click "deleteItem(p.id)", :type "button"}
     [:span.glyphicon.glyphicon-remove] ""]]]
  [:tr
   {:ng-if "editId===p.id", :ng-show "editId===p.id"}
   [:td
    {:ng-include "'edit-row.html'",
     :colspan "9"}]]]]
