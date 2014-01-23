[:h1 "Tables:"]
[:div.well {:ng-repeat "table in tables"}
 [:button.btn.btn-danger.pull-right.btn-sm
  {:type "button"
   :ng-click "removeTable(table.id)"}
  [:span.glyphicon.glyphicon-remove-circle]
  "delete"]
 [:h3 {:editable-text "table.name"}
  "{{table.name}}"]
 [:div.btn-form {:ng-show "tableForm.$visible"}
  [:div.input-group.col-lg-3
   [:input
    {:class "form-control"
     :placeholder "new column name..."
     :type "text" :ng-model "newColumnName"}]
   [:span.input-group-btn
    [:button.btn.btn-success
     {:type "button"
      :ng-disabled "tableForm.$waiting || !newColumnName"
      :ng-click
      "table.columns.push(newColumnName) && (newColumnName = '')"}
     [:span.glyphicon.glyphicon-plus-sign]
     "add column"]]]]
 [:br]
 [:div {:ng-include "'partials/table.html'"}]]
[:div.btn-edit
 [:br]
 [:button.btn.btn-success.btn-lg
  {:type "button"
   :ng-click "addTable()"}
  [:span.glyphicon.glyphicon-plus-sign]
  "create table"]]
