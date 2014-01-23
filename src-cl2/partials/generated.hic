[:h3 "Auto generated fields"]
[:table.table.table-bordered.table-hover.table-condensed
 [:tr {:style "font-weight: bold"}
  [:td {:style "width:35%"} "Name"]
  [:td {:style "width:65%"} "Value"]]
 [:tr
  {:ng-repeat
   "field in fields | filterAmountFields:config.amountSuffix"}
  [:td
   [:span "{{ field.name + config.amountIwSuffix }}"]]
  [:td
   [:span "{{ field.value | amountInWords}}"]]]]
