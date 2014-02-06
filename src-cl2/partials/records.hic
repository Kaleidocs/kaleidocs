[:h2 "Filter records by profile"]
[:div
 [:select.form-control
  {:ng-model "profileFilter"
   :ng-options "p.id as p.name for p in profiles"}
  [:option {:value ""}
   "-- no profile --"]]]

 [:div.row
  [:button.btn.btn-danger.btn-xs.pull-right
   {:ng-click "removeRecord(record.id)"
    :ng-hide "textBtnForm.$visible"}
   [:span.glyphicon.glyphicon-remove-circle]
   "Delete"]
  [:h3
   "Record #{{ record.id }}"]]
 [:div {:ng-include "'partials/record.html'"
        :ng-controller "recordCtrl"}]]
[:br]
[:button.btn.btn-success.btn-lg
 {:ng-click "addRecord()"}
 [:span.glyphicon.glyphicon-plus-sign]
 "Add record"]
