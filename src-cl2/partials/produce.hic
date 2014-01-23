[:h1 "Fields:"]
[:form {:editable-form ""
        :name "produceForm"
        :onaftersave "syncProduce()"
        :oncancel "reset()"}
 [:table.table.table-bordered.table-hover.table-condensed
  [:tr {:style "font-weight: bold"}
   [:td {:style "width:35%"} "Name"]
   [:td {:style "width:55%"} "Value"]
   [:td {:style "width:10%"}
    [:span {:ng-show "produceForm.$visible"}
     "Action"]]]
  [:tr {:ng-repeat "field in fields | filter:filterFields"}
   [:td
    [:span {:editable-text "field.name"
            :e-form "produceForm"
            ;;:onbeforesave "check"
            :e-required ""}
     "{{ field.name || 'empty' }}"]]
   [:td
    [:span {:editable-text "field.value"
            :e-form "produceForm"
            ;;:onbeforesave "check"
            :e-required ""}
     "{{ field.value || 'empty' }}"]]
   [:td [:button.btn.btn-danger.pull-right
         {:type "button"
          :ng-show "produceForm.$visible"
          :ng-click "removeField($index)"}
         "Del"]]]]
 [:div.btn-edit
  [:button.btn.btn-warning
   {:type "button"
    :ng-show "!produceForm.$visible"
    :ng-click "produceForm.$show()"}
   "edit"]]
 [:div.btn-form {:ng-show "produceForm.$visible"}
  [:button.btn.btn-success.pull-right
   {:type "button"
    :ng-disabled "produceForm.$waiting"
    :ng-click "addField()"}
   "add row"]
  [:button.btn.btn-primary
   {:type "submit"
    :ng-disabled "produceForm.$waiting"}
   "save"]
  [:button.btn.btn-default
   {:type "button"
    :ng-disabled "produceForm.$waiting"
    :ng-click "produceForm.$cancel()"}
   "cancel"]]]
[:div {:ng-controller "generatedCtrl"
       :ng-include "'partials/generated.html'"}]
