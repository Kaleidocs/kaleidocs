[:h3 "Fields:"]
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
            :e-required ""}
     "{{ field.name || 'empty' }}"]]
   [:td
    [:span {:editable-text "field.value"
            :e-form "produceForm"
            :e-required ""}
     "{{ field.value || 'empty' }}"]]
   [:td
    [:button.btn.btn-danger.btn-xs.pull-right
     {:type "button"
      :ng-show "produceForm.$visible"
      :ng-click "removeField($index)"}
     [:span.glyphicon.glyphicon-remove-circle]
     "remove"]]]]
 [:div.btn-edit
  [:button.btn.btn-warning
   {:type "button"
    :ng-show "!produceForm.$visible"
    :ng-click "produceForm.$show()"}
   [:span.glyphicon.glyphicon-pencil]
   "edit"]]
 [:div.btn-form {:ng-show "produceForm.$visible"}
  [:button.btn.btn-success.pull-right
   {:type "button"
    :ng-disabled "produceForm.$waiting"
    :ng-click "addField()"}
   [:span.glyphicon.glyphicon-plus-sign]
   "add row"]
  [:button.btn.btn-primary
   {:type "submit"
    :ng-disabled "produceForm.$waiting"}
   [:span.glyphicon.glyphicon-ok]
   "save"]
  [:button.btn.btn-default
   {:type "button"
    :ng-disabled "produceForm.$waiting"
    :ng-click "produceForm.$cancel()"}
   [:span.glyphicon.glyphicon-trash]
   "cancel"]]]
[:div {:ng-controller "generatedCtrl"
       :ng-include "'partials/generated.html'"}]
[:div {:ng-controller "selectCtrl"
       :ng-include "'partials/select.html'"}]
