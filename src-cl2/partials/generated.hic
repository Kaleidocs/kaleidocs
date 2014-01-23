[:h1 "Auto generated fields"]
[:table.table.table-bordered.table-hover.table-condensed
 [:tr {:style "font-weight: bold"}
  [:td {:style "width:35%"} "Name"]
  [:td {:style "width:65%"} "Value"]]
 [:tr
  {:ng-repeat
   "field in fields | filterAmountFields:config.amountSuffixes"}
  [:td
   [:span "{{ field.name + config.amountIwSuffixes }}"]]
  [:td
   [:span "{{ field.value | amountInWords}}"]]]]
[:button.btn.btn-success
 [:span.glyphicon.glyphicon-play]
 "Produce documents"]
