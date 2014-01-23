[:h1 "Tables:"]
[:div.well {:ng-repeat "table in tables"}
 [:button.btn.btn-danger.pull-right.btn-sm
  {:type "button"
   :ng-click "removeTable(table.id)"}
  [:span.glyphicon.glyphicon-remove-circle]
  "delete"]
 [:h3 {:editable-text "table.name"}
  "{{table.name}}"]
 [:div {:ng-include "'partials/table.html'"
        :ng-controller "tableCtrl"}]
 [:div {:ng-controller "generatedCtrl"
        :ng-include "'partials/table-generated.html'"}]]
[:div.btn-edit
 [:br]
 [:button.btn.btn-success.btn-lg
  {:type "button"
   :ng-click "addTable()"}
  [:span.glyphicon.glyphicon-plus-sign]
  "create table"]]
