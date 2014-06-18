[:form.form-horizontal.well
 [:div {:ng-hide "true"} "{{ p.date = (p.date|thenOrNow) }}"]
 [:div.form-group.has-feedback
  {:ng-repeat "key in foreignKeys"}
  [:label.col-sm-3.control-label
   "{{key.foreignType}}"]
  [:div
   {:ng-hide "true"}
   "{{ p[key.foreignType+'_id'] = p[key.foreignType+'_full'].id }}"]
  [:div.col-sm-6
   [:input.form-control
    {:name "{{key.foreignType}}",
     :ng-model "p[key.foreignType+'_full']"
     :ng-controller "queryCtrl"
     :typeahead "
entity
 as entity._details
 for entity in
  findEntity (key.foreignType, key.foreignKey, $viewValue)"}]
   [:span {:class "glyphicon glyphicon-search form-control-feedback"}]]]

 [:div.form-group
  {:ng-repeat "key in itemKeys"}
  [:label.col-sm-3.control-label
   {:ng-if "key != 'id'"}
   "{{key}}"]
  [:div.col-sm-6
   [:input.form-control
    {:name "{{key}}",
     :type "{{key | fieldType }}"
     :ng-model "p[key]"
     :ng-init "(key == 'records') ?
  watchRecords((p[key] | stringToSet)) :
  true"
     :ng-change "(key == 'records') ?
  watchRecords((p[key] | stringToSet)) :
  true"}]]]
 [:div
  [:table.table.table-bordered
   {:ng-if "p.records"}
   [:div {:ng-hide "true"}
    "{{ p.records ? (p.sum = fetchedData.sum) : (p.sum = 0)}}
     {{ recordColumns = ['id', 'money', 'remarks', 'date', 'company']}}"]
   [:thead
    [:tr
     [:th {:ng-repeat "k in recordColumns"}
      "{{k}}"]]]
   [:tbody
    [:tr {:ng-repeat "record in fetchedData.result"}
     [:td {:ng-repeat "k in recordColumns"}
      "{{record[k]}}"]]]]]
 [:div.form-group
  [:div.col-sm-offset-3.col-sm-6
   [:input.btn.btn-success
    {:ng-click "saveItem(p); setEditId(-1)",
     :value "{{p.id ? 'save': 'add'}}",
     :type "button"}]
   [:input.btn.btn-warning
    {:ng-if "p.id"
     :ng-click "setEditId(-1)",
     :value "cancel",
     :type "button"}]]]]
