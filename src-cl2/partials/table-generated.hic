[:h3 "Auto generated table fields"]
[:table.table.table-bordered.table-hover.table-condensed
 [:tr
  [:td [:h5 "ID"]]
  [:td
   {:ng-repeat
    "column in table.columns | filterAmountTableFields:config.amountSuffixes"}
   [:h5 "{{column.name + config.amountIwSuffixes }}"]]]
 [:tr {:ng-repeat "row in table.rows"}
  [:td "{{$index + 1}}"]
  [:td
   {:ng-repeat
    "column in table.columns | filterAmountTableFields:config.amountSuffixes"}
   [:span {:editable-text "row.values[column.id]"
           :e-form "tableForm"
           :e-required ""}
    "{{ row.values[column.id] | amountInWords }}"]]]]
[:div {:ng-show "false"}
 "{{ tableSum = (table | sumByColumn:config.sumColumn) }}"]
[:table.table.table-bordered.table-hover.table-condensed
 {:ng-if "tableSum"}
 [:tr {:style "font-weight: bold"}
  [:td {:style "width:35%"} "Name"]
  [:td {:style "width:65%"} "Value"]]
 [:tr
  [:td
   [:span "{{ table.name + '_SUM' }}"]]
  [:td
   [:span "{{ tableSum }}"]]]
 [:tr
  [:td
   [:span "{{ table.name + '_SUM' + config.amountIwSuffixes }}"]]
  [:td
   [:span "{{ tableSum | amountInWords }}"]]]]
