[:form.form-horizontal
 [:div {:ng-hide "true"} "{{ p.date = (p.date|thenOrNow) }}"]
 [:div.form-group
  {:ng-repeat "key in foreignKeys"}
  [:label.col-sm-2.control-label
   "{{key.foreignType}}"]
  [:div
   {:ng-hide "true"}
   "{{ p[key.foreignType+'_id'] = p[key.foreignType+'_full'].id }}"]
  [:input.w100
   {:name "{{key.foreignType}}",
    ;; :type "number"
    :ng-model "p[key.foreignType+'_full']"
    :ng-controller "queryCtrl"
    :typeahead "
entity
 as entity._details
 for entity in
  findEntity (key.foreignType, key.foreignKey, $viewValue)"}]]

 [:div.form-group
  {:ng-repeat "key in itemKeys"}
  [:label.col-sm-2.control-label
   {:ng-if "key != 'id'"}
   "{{key}}"]
  [:input.w100
   {:name "{{key}}",
    :type "{{key | fieldType }}"
    :ng-model "p[key]"
    :ng-change "(key == 'records') ?
  watchRecords((p[key] | stringToSet)) :
  true"}]]
 [:div
  [:table.table.table-bordered
   {:ng-if "p.records"}
   [:div {:ng-hide "true"}
    "{{p.sum = fetchedData.sum}}"]
   [:thead
    [:tr
     [:th {:ng-repeat "k in ['id', 'money']"}
      "{{k}}"]]]
   [:tbody
    [:tr {:ng-repeat "record in fetchedData.result"}
     [:td {:ng-repeat "k in ['id', 'money']"}
      "{{record[k]}}"]]]]]
 [:div.form-group
  [:div.col-sm-offset-2.col-sm-10
   [:input.btn.btn-default
    {:ng-click "saveItem(p); setEditId(-1)",
     :value "{{p.id ? 'save': 'add'}}",
     :type "button"}]
   [:input.btn.btn-default
    {:ng-if "p.id"
     :ng-click "setEditId(-1)",
     :value "cancel",
     :type "button"}]]]]
