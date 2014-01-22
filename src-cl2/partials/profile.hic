[:form {:editable-form ""
        :name "profileForm"
        :onaftersave "syncProfile()"
        :oncancel "reset()"}
 [:table.table.table-bordered.table-hover.table-condensed
  [:tr {:style "font-weight: bold"}
   [:td {:style "width:35%"} "Name"]
   [:td {:style "width:55%"} "Value"]
   [:td {:style "width:10%"}
    [:span {:ng-show "profileForm.$visible"}
     "Action"]]]
  [:tr {:ng-repeat "field in fields | filter:filterFields"}
   [:td
    [:span {:editable-text "field.name"
            :e-form "profileForm"
            ;;:onbeforesave "check"
            :e-required ""}
     "{{ field.name || 'empty' }}"]]
   [:td
    [:span {:editable-text "field.value"
            :e-form "profileForm"
            ;;:onbeforesave "check"
            :e-required ""}
     "{{ field.value || 'empty' }}"]]
   [:td [:button.btn.btn-danger.btn-xs.pull-right
         {:type "button"
          :ng-show "profileForm.$visible"
          :ng-click "removeField($index)"}
         [:span.glyphicon.glyphicon-remove-circle]
         "delete"]]]]
 [:div.btn-edit
  [:button.btn.btn-warning
   {:type "button"
    :ng-show "!profileForm.$visible"
    :ng-click "profileForm.$show()"}
   [:span.glyphicon.glyphicon-pencil]
   "edit"]]
 [:div.btn-form {:ng-show "profileForm.$visible"}
  [:button.btn.btn-success.pull-right
   {:type "button"
    :ng-disabled "profileForm.$waiting"
    :ng-click "addField()"}
   [:span.glyphicon.glyphicon-plus-sign]
   "add field"]
  [:button.btn.btn-primary.btn-lg
   {:type "submit"
    :ng-disabled "profileForm.$waiting"}
   [:span.glyphicon.glyphicon-ok]
   "save"]
  [:button.btn.btn-default.btn-lg
   {:type "button"
    :ng-disabled "profileForm.$waiting"
    :ng-click "profileForm.$cancel()"}
   [:span.glyphicon.glyphicon-trash]
   "cancel"]]]
