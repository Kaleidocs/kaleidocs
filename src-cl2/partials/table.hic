[:form {:editable-form ""
        :name "tableForm"
        :onaftersave "syncTable(table.id, table)"
        :oncancel "reset(table.id, table)"}
 [:table.table.table-bordered.table-hover.table-condensed
  [:tr
   [:td {:ng-repeat "column in table.columns"} "{{column}}"]
   [:td {:style "width:10%"}
    [:span {:ng-show "tableForm.$visible"}
     "Action"]]]
  [:tr {:ng-repeat "field in table.fields | filterDeleted"
        :ng-init "fieldIndex = $index"}
   [:td {:ng-repeat "column in table.columns"
         :ng-init "columnIndex = $index"}
    [:span {:editable-text "field[column]"
            :e-form "tableForm"
            ;;:onbeforesave "check"
            :e-required ""}
     "{{ field[column] || 'empty' }}"]]
   [:td [:button.btn.btn-danger.btn-xs.pull-right
         {:type "button"
          :ng-show "tableForm.$visible"
          :ng-click "table.fields[fieldIndex] = 'deleted'"}
         [:span.glyphicon.glyphicon-remove-circle]
         "Delete row {{fieldIndex}}"]]]]
 [:button.btn.btn-warning
  {:type "button"
   :ng-show "!tableForm.$visible"
   :ng-click "tableForm.$show()"}
  [:span.glyphicon.glyphicon-pencil]
  "edit"]
 [:div.btn-form {:ng-show "tableForm.$visible"}
  [:button.btn.btn-success.pull-right
   {:type "button"
    :ng-disabled "tableForm.$waiting"
    :ng-click "table.fields.push({})"}
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
