[:form.form-horizontal {:role "form"}
 [:div.form-group
  [:label.col-sm-4.control-label "Suffix for amount of money"]
  [:div.col-sm-8
   [:input.form-control
    {:type "text"
     :ng-model "config.amountSuffix"
     :ng-trim "true"
     :ng-pattern "/^[a-zA-Z0-9\\_]*$/"}]]]
 [:div.form-group
  [:label.col-sm-4.control-label "Suffix for amounts in words"]
  [:div.col-sm-8
   [:input.form-control
    {:type "text"
     :ng-model "config.amountIwSuffix"
     :ng-trim "true"
     :ng-pattern "/^[a-zA-Z0-9\\_]*$/"}]]]
 [:div.form-group
  [:label.col-sm-4.control-label "Column to calculate sum"]
  [:div.col-sm-8
   [:input.form-control
    {:type "text"
     :ng-model "config.sumColumn"
     :ng-trim "true"
     :ng-pattern "/^[a-zA-Z0-9\\_]*$/"}]]]
 [:div.form-group
  [:label.col-sm-4.control-label "Default profile keys"]
  [:div.col-sm-8
   [:tags-input {:ng-model "config.profileKeys"
                 :add-on-space "true"
                 :min-length "1"
                 :allowed-tags-pattern "^[a-zA-Z0-9\\_]*$"}]]]
 [:div.form-group
  [:label.col-sm-4.control-label "Default record keys"]
  [:div.col-sm-8
   [:tags-input {:ng-model "config.recordKeys"
                 :add-on-space "true"
                 :min-length "1"
                 :allowed-tags-pattern "^[a-zA-Z0-9\\_]*$"}]]]
 [:div.form-group
  [:label.col-sm-4.control-label "Auto-save interval (minutes)"]
  [:div.col-sm-8
   [:input.form-control {:ng-model "config.saveInterval"
                         :type "number"
                         :min "0"}]]]
 [:div.form-group
  [:label.col-sm-4.control-label "Default table keys"]
  [:div.col-sm-8
   [:tags-input {:ng-model "config.tableKeys"
                 :add-on-space "true"
                 :min-length "1"
                 :allowed-tags-pattern "^[a-zA-Z0-9\\_]*$"}]]]
 [:div.form-group
  [:label.col-sm-4.control-label "Search columns"]
  [:div.col-sm-8
   [:tags-input {:ng-model "config.searchColumns"
                 :add-on-space "true"
                 :min-length "1"
                 :allowed-tags-pattern "^[a-zA-Z0-9\\_]*$"}]]]]
