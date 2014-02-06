[:h3 "Auto generated table"]
[:table.table.table-bordered.table-hover.table-condensed
 {:ng-repeat "record in produce.records"
  :ng-init "rowId = $index"}
 [:tr {:style "font-weight: bold"}
  [:td {:style "width:35%"} "Name"]
  [:td {:style "width:65%"} "Value"]]
 [:tr
  [:td [:span "$TABLE.ID"]]
  [:td [:span "{{rowId}}"]]]
 [:tr
  {:ng-repeat
   "field in record.fields | filterAmountFields:config.amountSuffix"}
  [:td
   [:span "$TABLE.{{ field.name + config.amountIwSuffix }}"]]
  [:td
   [:span "{{ field.value | amountInWords}}"]]]]
[:br]
[:h3 "Auto generated fields"]
[:div {:ng-show "false"}
 "{{ tableSum = (produce.records | sumOfColumn:config.sumColumn)}}
  {{ tableSumIW = (tableSum | amountInWords) }}
  {{ tableSumTitle = ('TABLE' + '_SUM') }}
  {{ tableSumIWTitle = (tableSumTitle + config.amountIwSuffix) }}
  {{ produce.table =  exportTable(tableSumTitle, tableSumIWTitle, tableSum, tableSumIW)}}"]
[:table.table.table-bordered.table-hover.table-condensed
 [:tr {:style "font-weight: bold"}
  [:td {:style "width:35%"} "Name"]
  [:td {:style "width:65%"} "Value"]]
 [:tr
  [:td
   [:span "${{ tableSumTitle }}"]]
  [:td
   [:span "{{ tableSum }}"]]]
 [:tr
  [:td
   [:span "${{ tableSumIWTitle }}"]]
  [:td
   [:span "{{ tableSumIW }}"]]]]
[:br]
[:button.btn.btn-success.btn-lg
 {:ng-click "genDoc(getSingleTemplates(), getMultipleTemplates(), config.tableKeys, produce.profile, produce.table, exportRecords(produce.records))"}
 [:span.glyphicon.glyphicon-play]
 "Produce documents"]
