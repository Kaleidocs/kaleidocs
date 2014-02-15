[:h2 "Filter records by profile"]
[:div
 [:input.form-control
  {:type "text" :ng-model "profileFilter"
   :typeahead "p as p.name for p in
  ( profiles | filter: ({name: $viewValue}) | limitTo: 8 )"}]]
[:div
 {:ng-repeat "record in ( records | filterByProfile: profileFilter.id )"}
 [:div.row
  [:h3
   "Record #{{ record.id }}"
   [:button.btn.btn-danger.btn-xs.pull-right
    {:ng-click "removeRecord(record.id)"
     :ng-hide "textBtnForm.$visible"}
    [:span.glyphicon.glyphicon-remove-circle]
    "Delete"]]]
 [:div {:ng-include "'partials/record.html'"
        :ng-controller "recordCtrl"}]]

[:br]
[:button.btn.btn-success.btn-lg
 {:ng-click "addRecord(profileFilter.id)"
  :ng-disabled "profileFilter.id === undefined"}
 [:span.glyphicon.glyphicon-plus-sign]
 "Add record"]
