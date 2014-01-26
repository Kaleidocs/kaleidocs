[:form {:editable-form ""
        :name "templateForm"
        :onaftersave "syncTemplate()"
        :oncancel "reset()"}
 [:table.table.table-bordered.table-hover.table-condensed
  [:tr {:style "font-weight: bold"}
   [:td {:style "width:35%"} "Name"]
   [:td {:style "width:55%"} "Value"]
   [:td {:style "width:10%"}
    [:span {:ng-show "templateForm.$visible"}
     "Action"]]]
  [:tr {:ng-repeat "field in fields | filter:filterFields"}
   [:td
    [:span {:editable-text "field.name"
            :e-form "templateForm"
            ;;:onbeforesave "check"
            :e-required ""}
     "{{ field.name || 'empty' }}"]]
   [:td
    [:span {:editable-text "field.value"
            :e-form "templateForm"
            ;;:onbeforesave "check"
            :e-required ""}
     "{{ field.value || 'empty' }}"]]
   [:td [:button.btn.btn-danger.btn-xs.pull-right
         {:type "button"
          :ng-show "templateForm.$visible"
          :ng-click "removeField($index)"}
         [:span.glyphicon.glyphicon-remove-circle]
         "delete"]]]]
 [:div.btn-edit
  [:button.btn.btn-warning
   {:type "button"
    :ng-show "!templateForm.$visible"
    :ng-click "templateForm.$show()"}
   [:span.glyphicon.glyphicon-pencil]
   "edit"]]
 [:div.btn-form {:ng-show "templateForm.$visible"}
  [:button.btn.btn-success.pull-right
   {:type "button"
    :ng-disabled "templateForm.$waiting"
    :ng-click "addField()"}
   [:span.glyphicon.glyphicon-plus-sign]
   "add field"]
  [:button.btn.btn-primary
   {:type "submit"
    :ng-disabled "templateForm.$waiting"}
   [:span.glyphicon.glyphicon-ok]
   "save"]
  [:button.btn.btn-default
   {:type "button"
    :ng-disabled "templateForm.$waiting"
    :ng-click "templateForm.$cancel()"}
   [:span.glyphicon.glyphicon-trash]
   "cancel"]]]
