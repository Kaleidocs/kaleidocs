[:form {:editable-form ""
        :name "tableForm"
        :onaftersave "syncTable()"
        :oncancel "loadTable()"}
 [:div.input-group.col-lg-3.pull-right
  {:ng-show "tableForm.$visible"}
  [:input
   {:class "form-control"
    :placeholder "new column name..."
    :type "text" :ng-model "newColumnName"}]
  [:span.input-group-btn
   [:button.btn.btn-success
    {:type "button"
     :ng-disabled "tableForm.$waiting || !newColumnName"
     :ng-click
     "addColumn(newColumnName) ; (newColumnName = '')"}
    [:span.glyphicon.glyphicon-plus-sign]
    "add column"]]]
 [:br]
 [:br]
 [:table.table.table-bordered.table-hover.table-condensed
  [:tr
   [:td {:ng-repeat "column in table.columns"}
    [:h5 "{{column.name}} "
     [:span.glyphicon.glyphicon-minus-sign.text-danger
      {:ng-click "removeColumn(column.id)"
       :ng-show "tableForm.$visible"
       :title "remove column"}]]]
   [:td {:style "width:10%"}
    [:span {:ng-show "tableForm.$visible"}
     "Action"]]]
  [:tr {:ng-repeat "row in table.rows"}
   [:td {:ng-repeat "column in table.columns"}
    [:span {:editable-text "row.values[column.id]"
            :e-form "tableForm"
            ;;:onbeforesave "check"
            :e-required ""}
     "{{ row.values[column.id] || 'empty' }}"]]
   [:td [:button.btn.btn-danger.btn-xs.pull-right
         {:type "button"
          :ng-show "tableForm.$visible"
          :ng-click "removeRow(row.id)"}
         [:span.glyphicon.glyphicon-remove-circle]
         "Delete"]]]]
 [:button.btn.btn-warning
  {:type "button"
   :ng-show "!tableForm.$visible"
   :ng-click "loadTable ; tableForm.$show()"}
  [:span.glyphicon.glyphicon-pencil]
  "edit"]
 [:div.btn-form {:ng-show "tableForm.$visible"}
  [:button.btn.btn-success.pull-right
   {:type "button"
    :ng-disabled "tableForm.$waiting"
    :ng-click "addRow()"}
   [:span.glyphicon.glyphicon-plus-sign]
   "add row"]
  [:button.btn.btn-primary
   {:type "submit"
    :ng-disabled "tableForm.$waiting"}
   [:span.glyphicon.glyphicon-ok]
   "save"]
  [:button.btn.btn-default
   {:type "button"
    :ng-disabled "tableForm.$waiting"
    :ng-click "tableForm.$cancel()"}
   [:span.glyphicon.glyphicon-trash]
   "cancel"]]]
