[:h3 "Auto generated fields"]
[:table.table.table-bordered.table-hover.table-condensed
 {:ng-repeat "record in produce.records"}
 [:tr [:td [:h4 "{{record.name}}"]]]
 [:tr {:style "font-weight: bold"}
  [:td {:style "width:35%"} "Name"]
  [:td {:style "width:65%"} "Value"]]
 [:tr
  {:ng-repeat
   "field in record.fields | filterAmountFields:config.amountSuffix"}
  [:td
   [:span "{{ field.name + config.amountIwSuffix }}"]]
  [:td
   [:span "{{ field.value | amountInWords}}"]]]]
[:br]
[:h3 "Auto generated table"]
[:div {:ng-show "false"}
 "{{tableSum = (produce.records | sumOfColumn:config.sumColumn)}}"]
[:table.table.table-bordered.table-hover.table-condensed
 [:tr {:style "font-weight: bold"}
  [:td {:style "width:35%"} "Name"]
  [:td {:style "width:65%"} "Value"]]
 [:tr
  [:td
   [:span "{{ 'TABLE' + '_SUM' }}"]]
  [:td
   [:span "{{ tableSum }}"]]]
 [:tr
  [:td
   [:span "{{ 'TABLE' + '_SUM' + config.amountIwSuffix }}"]]
  [:td
   [:span "{{ tableSum | amountInWords }}"]]]]
