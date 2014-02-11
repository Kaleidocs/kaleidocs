[:h3 "Multiple Templates"]
[:label {:ng-repeat "t_ in multipleTemplates"}
 [:input {:type "checkbox"
          :ng-model "t_.checked"}]
 "  {{ t_.filename }}"]

[:h3 "Single Templates"]
[:label {:ng-repeat "t_ in singleTemplates"}
 [:input {:type "checkbox"
          :ng-model "t_.checked"}]
 "  {{ t_.filename }}"]

[:h3 "Records:"]

[:div.form-group
  [:label.col-sm-4.control-label "Record IDs"]
  [:div.col-sm-8
   [:tags-input {:ng-model "produce.recordIds"
                 :add-on-space "true"
                 :min-length "1"
                 :allowed-tags-pattern "^[0-9]*$"}]]]
[:div {:ng-show "false"}
 "{{produce.records = getRecords(produce.recordIds)}}"]
[:div {:ng-show "false"}
 "{{profile = getProfile(produce.records)}}"]
[:div {:ng-show "false"}
 "{{produce.profile = (profile.fields | fieldsToMap) }}"]
[:h3 "Profile:"]
[:table.table.table-bordered.table-hover.table-condensed
  [:tr {:style "font-weight: bold"}
   [:td {:style "width:35%"} "Name"]
   [:td {:style "width:55%"} "Value"]]
  [:tr {:ng-repeat "field in profile.fields"}
   [:td
    [:span
     "${{ field.name || 'empty' }}"]]
   [:td
    [:span
     "{{ field.value || 'empty' }}"]]
   ]]
[:div {:ng-controller "generatedCtrl"
       :ng-include "'partials/generated.html'"}]
