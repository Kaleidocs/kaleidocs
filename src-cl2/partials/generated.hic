[:h3 "Auto generated row values"]
[:table.table.table-bordered.table-hover.table-condensed
 [:tr
  [:td [:span "$TABLE.ID"]]
  [:td
   {:ng-repeat
    "field in produce.records[0].fields | filterAmountFields:config.amountSuffix"}
   [:span "$TABLE.{{ field.name + config.amountIwSuffix }}"]]]
 [:tr
  {:ng-repeat "record in produce.records"
   :ng-init "rowId = $index"}
  [:td [:span "{{rowId}}"]]
  [:td
   {:ng-repeat
    "field in record.fields | filterAmountFields:config.amountSuffix"}
   [:span "{{ field.value | amountInWords}}"]]]]

[:h3 "Auto generated fields"]
[:div {:ng-show "false"}
 "{{ tableSum = (produce.records | sumOfColumn:config.sumColumn)}}
  {{ tableSumAsNumber = (tableSum | number) }}
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
   [:span "{{ tableSumAsNumber }}"]]]
 [:tr
  [:td
   [:span "${{ tableSumIWTitle }}"]]
  [:td
   [:span "{{ tableSumIW }}"]]]
 [:tr
  [:td
   [:span "$PID"]]
  [:td
   [:span "{{ PID }}"]]]
 [:tr
  [:td
   [:span "$DD/$MM/$YYYY"]]
  [:td
   [:span "{{ DD }}/{{ MM }}/{{ YYYY }}"]]]]

[:br]
[:button.btn.btn-success.btn-lg
 {:ng-click "genDoc(getSingleTemplates(), getMultipleTemplates(), config.tableKeys, produce.profile, produce.table, exportRecords(produce.records), getAutoFields())"}
 [:span.glyphicon.glyphicon-play]
 "Produce documents"]
