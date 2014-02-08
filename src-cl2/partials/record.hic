[:div.row
 [:div.col-xs-4
  [:select.form-control
   {:ng-model "record.profile"
    :ng-options "p.id as p.name for p in profiles"}
   [:option {:value ""} "-- choose a profile --"]]]]

[:form {:editable-form ""
        :name "recordForm"
        :onaftersave "syncRecord()"
        :oncancel "reset()"}
 [:table.table.table-bordered.table-hover.table-condensed
  [:tr {:style "font-weight: bold"}
   [:td {:style "width:35%"} "Name"]
   [:td {:style "width:55%"} "Value"]
   [:td {:style "width:10%"}
    [:span {:ng-show "recordForm.$visible"}
     "Action"]]]
  [:tr {:ng-repeat "field in fields | filter:filterFields"}
   [:td
    [:span {:editable-text "field.name"
            :e-form "recordForm"
            ;;:onbeforesave "check"
            :e-required ""}
     "${{ field.name || 'empty' }}"]
    [:span.glyphicon.glyphicon-star.text-danger
     {:ng-show "field.name | amountField_p"} " "]]
   [:td
    [:span {:editable-text "field.value"
            :e-form "recordForm"
            ;;:onbeforesave "check"
            :e-required ""}
     ;; in Lisp language:
     #_(or (if (amount-field? field.name)
           (field.name | number)
           field.name)
         "empty")
     "{{ ((field.name | amountField_p)
         ? ( (field.value | number)
           + ' ('
           +  (field.value | amountInWords)
           + ')' )
         : (field.value))
        || 'empty' }}"]]
   [:td [:button.btn.btn-danger.btn-xs.pull-right
         {:type "button"
          :ng-show "recordForm.$visible"
          :ng-click "removeField($index)"}
         [:span.glyphicon.glyphicon-remove-circle]
         "delete"]]]]

 [:div.btn-edit
  [:button.btn.btn-warning
   {:type "button"
    :ng-show "!recordForm.$visible"
    :ng-click "recordForm.$show()"}
   [:span.glyphicon.glyphicon-pencil]
   "edit"]]
 [:div.btn-form {:ng-show "recordForm.$visible"}
  [:button.btn.btn-success.pull-right
   {:type "button"
    :ng-disabled "recordForm.$waiting"
    :ng-click "addField()"}
   [:span.glyphicon.glyphicon-plus-sign]
   "add field"]
  [:button.btn.btn-primary
   {:type "submit"
    :ng-disabled "recordForm.$waiting"}
   [:span.glyphicon.glyphicon-ok]
   "save"]
  [:button.btn.btn-default
   {:type "button"
    :ng-disabled "recordForm.$waiting"
    :ng-click "recordForm.$cancel()"}
   [:span.glyphicon.glyphicon-trash]
   "cancel"]]]
