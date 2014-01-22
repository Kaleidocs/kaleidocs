(load-file "angular-cl2/src/core.cl2")
(load-file "atom-crud/src/core.cl2")
(load-file "../test-cl2/sample_data.cl2")

(defapp my-app [ngRoute xeditable])

;; don't have to specify app name as compiler remember the last app name
;; defined in `defapp`
(defroute
  ;;"/default" ['my-ctrl "partials/default.html"]
  "/default"
  {:controller 'empty-ctrl
   :template
   (hiccup
    [:div "{{tables}}"]
    [:div "{{config}}"]
    [:div "{{profiles}}"])}
  "/profile/:profileId"
  {:controller 'profile-ctrl
   :template
   (hiccup
    [:div "{{fields}}"]
    [:form {:editable-form ""
            :name "profileForm"
            :onaftersave "syncProfile()"
            :oncancel "reset()"}
     [:table.table.table-bordered.table-hover.table-condensed
      [:tr {:style "font-weight: bold"}
       [:td {:style "width:35%"} "Name"]
       [:td {:style "width:55%"} "Value"]
       [:td {:style "width:10%"}
        [:span {:ng-show "profileForm.$visible"}
         "Action"]]]
      [:tr {:ng-repeat "field in fields | filter:filterFields"}
       [:td
        [:span {:editable-text "field.name"
                :e-form "profileForm"
                ;;:onbeforesave "check"
                :e-required ""}
         "{{ field.name || 'empty' }}"]]
       [:td
        [:span {:editable-text "field.value"
                :e-form "profileForm"
                ;;:onbeforesave "check"
                :e-required ""}
         "{{ field.value || 'empty' }}"]]
       [:td [:button.btn.btn-danger.pull-right
             {:type "button"
              :ng-show "profileForm.$visible"
              :ng-click "removeField($index)"}
             "Del"]]]]
     [:div.btn-edit
      [:button.btn.btn-default
       {:type "button"
        :ng-show "!profileForm.$visible"
        :ng-click "profileForm.$show()"}
       "edit"]]
     [:div.btn-form {:ng-show "profileForm.$visible"}
      [:button.btn.btn-default.pull-right
       {:type "button"
        :ng-disabled "profileForm.$waiting"
        :ng-click "addField()"}
       "add row"]
      [:button.btn.btn-primary
       {:type "submit"
        :ng-disabled "profileForm.$waiting"}
       "save"]
      [:button.btn.btn-default
       {:type "button"
        :ng-disabled "profileForm.$waiting"
        :ng-click "profileForm.$cancel()"}
       "cancel"]]])}

  "/profiles"
  {:controller 'profiles-ctrl
   :template
   (hiccup
    [:div {:ng-repeat "profile in profiles"}
     [:div "{{profile}}"]
     [:div
      [:h3 {:editable-text "profile.name"
            :e-form "textBtnForm"}
       "{{ profile.name || 'empty' }}"
       [:button.btn.btn-default
        {:ng-click "textBtnForm.$show()"
         :ng-hide "textBtnForm.$visible"}
        "Rename"]
       [:button.btn.btn-danger
        {:ng-click "removeProfile(profile.id)"
         :ng-hide "textBtnForm.$visible"}
        "Delete"]
       [:a.btn.btn-default
        {:ng-hide "textBtnForm.$visible"
         :href "{{ '#/profile/' + profile.id }}"} "Edit"]]]]
    [:button.btn.btn-default
     {:ng-click "addProfile()"}
     "Add profile"])}
  "/produce"
  {:controller 'nil-ctrl
   :template
   (hiccup
    [:div {:ng-controller "tablesCtrl"}
     "{{tables}}"
     [:div {:ng-repeat "table in tables | filterDeleted"
            :ng-init "tableIndex = $index"}
      [:h3
       "Table: {{table.name}}{{tableIndex}}"]
      [:form {:editable-form ""
              :name "tableForm"
              :onaftersave "syncTable(tableIndex, table)"
              :oncancel "reset(tableIndex, table)"}
       [:table.table.table-bordered.table-hover.table-condensed
        [:tr
         [:td {:ng-repeat "column in table.columns"} "{{column}}"]
         [:td {:style "width:10%"}
          [:span {:ng-show "tableForm.$visible"}
           "Action"]]]
        [:tr {:ng-repeat "field in table.fields | filterDeleted"
              :ng-init "fieldIndex = $index"}
         [:td {:ng-repeat "column in table.columns"
               :ng-init "columnIndex = $index"}
          [:span {:editable-text "field[column]"
                  :e-form "tableForm"
                  ;;:onbeforesave "check"
                  :e-required ""}
           "{{ field[column] || 'empty' }}"]]
         [:td [:button.btn.btn-danger.pull-right
               {:type "button"
                :ng-show "tableForm.$visible"
                :ng-click "table.fields[fieldIndex] = 'deleted'"}
               "Delete row {{fieldIndex}}"]]]]
       [:div.btn-edit
        [:button.btn.btn-warning
         {:type "button"
          :ng-show "!tableForm.$visible"
          :ng-click "tableForm.$show()"}
         "edit table {{table.name}}"]]
       [:div.btn-form {:ng-show "tableForm.$visible"}
        [:button.btn.btn-success.pull-right
         {:type "button"
          :ng-disabled "tableForm.$waiting || !newColumnName"
          :ng-click
          "table.columns.push(newColumnName) && (newColumnName = '')"}
         "add column"]
        [:input.pull-right {:type "text" :ng-model "newColumnName"}]
        [:button.btn.btn-success.pull-right
         {:type "button"
          :ng-disabled "tableForm.$waiting"
          :ng-click "table.fields.push({})"}
         "add row"]
        [:button.btn.btn-primary
         {:type "submit"
          :ng-disabled "tableForm.$waiting"}
         "save"]
        [:button.btn.btn-default
         {:type "button"
          :ng-disabled "tableForm.$waiting"
          :ng-click "tableForm.$cancel()"}
         "cancel"]]
       [:div.btn-edit
        [:button.btn.btn-danger
         {:type "button"
          :ng-click "tables[tableIndex] = 'deleted'"}
         "delete table {{table.name}}"]]]]]

    [:div {:ng-controller "produceCtrl"}
     [:h3 "Fields: {{fields}}"]
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
                 ;;:onbeforesave "check"
                 :e-required ""}
          "{{ field.name || 'empty' }}"]]
        [:td
         [:span {:editable-text "field.value"
                 :e-form "produceForm"
                 ;;:onbeforesave "check"
                 :e-required ""}
          "{{ field.value || 'empty' }}"]]
        [:td [:button.btn.btn-danger.pull-right
              {:type "button"
               :ng-show "produceForm.$visible"
               :ng-click "removeField($index)"}
              "Del"]]]]
      [:div.btn-edit
       [:button.btn.btn-default
        {:type "button"
         :ng-show "!produceForm.$visible"
         :ng-click "produceForm.$show()"}
        "edit"]]
      [:div.btn-form {:ng-show "produceForm.$visible"}
       [:button.btn.btn-default.pull-right
        {:type "button"
         :ng-disabled "produceForm.$waiting"
         :ng-click "addField()"}
        "add row"]
       [:button.btn.btn-primary
        {:type "submit"
         :ng-disabled "produceForm.$waiting"}
        "save"]
       [:button.btn.btn-default
        {:type "button"
         :ng-disabled "produceForm.$waiting"
         :ng-click "produceForm.$cancel()"}
        "cancel"]]]])}
  "/config"
  {:controller 'config-ctrl
   :template
   (hiccup
    [:div "{{config}}"]
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
        "Cancel"]]]])}
  :default "/default")

(defdirective my-directive
  []
  ;; can be a directive-definition object or a link function
  (fn [scope elm attrs]
    (.
     scope
     ($watch
      (:my-directive attrs)
      (fn [value] (. elm (text (+ value 4))))))))

(defcontroller my-ctrl
  [$scope my-service]
  (def$ some-number 12)
  (defn$ add-two [n] {:result (+ n 2)})
  (defn$ service-add [n]
    (my-service.add-three n)))

(defcontroller profiles-ctrl
  [$scope]
  ($->atom profiles profiles)
  (defn$ remove-profile [id]
    (remove-entity! profiles id))
  (defn$ add-profile []
    (let [profile-id (gen-unique-id :profiles)
          default-keys (.split (get @config :profile-keys "") " ")]
      (add-entity! profiles
                   nil
                   {:id profile-id
                    :name "New profile"
                    :fields
                    (map (fn [k] {:name k
                                  :value ""})
                         default-keys)}))))

(defcontroller profile-ctrl
  [$scope $routeParams]
  (def profile-id (parseInt (:profile-id $routeParams)))
  (defn$ filter-field [field]
    (true? (:deleted? field)))
  (defn$ reset []
    (def$ fields
      (-> profiles
          (find-entities {:id profile-id})
          first
          (get :fields))))
  (($- reset))
  (defn$ sync-profile []
    (swap! profiles
           #(assoc-in % [profile-id :fields] ($- fields))))
  (defn$ remove-field [index]
    (.splice ($- fields)
             index 1))
  (defn$ add-field []
    (def$ fields
      (conj ($- fields)
            {:name "" :value ""}))))

(defcontroller produce-ctrl
  [$scope]
  (defn$ filter-field [field]
    (true? (:deleted? field)))
  (defn$ reset []
    (def$ fields @produce))
  (($- reset))
  (defn$ sync-produce []
    (reset! produce ($- fields)))
  (defn$ remove-field [index]
    (.splice ($- fields)
             index 1))
  (defn$ add-field []
    (def$ fields
      (conj ($- fields)
            {:name "" :value ""}))))

(def config
  (atom {:profile-keys "NAME ADDRESS",
         :search-columns "NAME ADDRESS",
         :produce-keys "CITY CONTRACT",
         :amount-suffixes "_AMOUNT",
         :amount-iw-suffixes "_IW",
         :table-keys "TASK MONEY_AMOUNT",
         :sum-column "MONEY_AMOUNT"}))

(defcontroller config-ctrl
  [$scope]
  ($->atom config config))

(defcontroller empty-ctrl
  [$scope]
  ($->atom tables tables)
  ($->atom profiles profiles)
  ($->atom config config))

(defcontroller nil-ctrl
  [$scope])

(defcontroller tables-ctrl
  [$scope]
  ($->atom tables tables)
  (defn$ reset-table [table-index]
    (def$ tables @tables)))

;; example of specifying app name
(defservice my-app my-service
 []
 (this->!)
 (defn! add-three [n] (+ n 3)))

;; generic defmodule usage
(defmodule my-app
  (:filter (my-filter [] [s] (+ s 5))))

(deffilter your-filter []
  [s]
  (+ s 6))

(deffilter filter-deleted []
  [items]
  (filter #(not (= :deleted %)) items))
