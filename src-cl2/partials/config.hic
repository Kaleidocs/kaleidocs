[:form
 {:editable-form ""
  :name "configForm"}
 [:div
  [:span.title "Suffixes for amount of money"]
  [:span {:editable-text "config.amountSuffixes"
          :e-name "amountSuffixes"
          ;;:onbeforesave ""
          :e-required ""}
   "{{ config.amountSuffixes || 'empty' }}"]]
 [:div
  [:span.title "Suffixes for amounts in words"]
  [:span {:editable-text "config.amountIwSuffixes"
          :e-name "amountIwSuffixes"
          ;;:onbeforesave ""
          :e-required ""}
   "{{ config.amountIwSuffixes || 'empty' }}"]]
 [:div
  [:span.title "Columns to calculate sum"]
  [:span {:editable-text "config.sumColumn"
          :e-name "sumColmn"}
   "{{ config.sumColumn || 'empty' }}"]]
 [:div
  [:span.title "Default profile keys"]
  [:span {:editable-text "config.profileKeys"
          :e-name "profileKeys"}
   "{{ config.profileKeys || 'empty' }}"]]
 [:div
  [:span.title "Default produce keys"]
  [:span {:editable-text "config.produceKeys"
          :e-name "produceKeys"}
   "{{ config.produceKeys || 'empty' }}"]]
 [:div
  [:span.title "Default table keys"]
  [:span {:editable-text "config.tableKeys"
          :e-name "tableKeys"}
   "{{ config.tableKeys || 'empty' }}"]]
 [:div
  [:span.title "Search columns"]
  [:span {:editable-text "config.searchColumns"
          :e-name "searchColumns"}
   "{{ config.searchColumns || 'empty' }}"]]
 [:div.buttons
  [:button.btn.btn-default
   {:type "button"
    :ng-click "configForm.$show()"
    :ng-show "!configForm.$visible"}
   "Edit"]
  [:span {:ng-show "configForm.$visible"}
   [:button.btn.btn-primary
    {:type "submit"
     :ng-disabled "configForm.$waiting"}
    "Save"]
   [:button.btn.btn-default
    {:type "submit"
     :ng-disabled "configForm.$waiting"
     :ng-click "configForm.$cancel()"}
    "Cancel"]]]]
