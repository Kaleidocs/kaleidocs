(load-file "angular-cl2/src/core.cl2")
(load-file "atom-crud/src/core.cl2")
(load-file "../test-cl2/sample_data.cl2")

(defapp my-app [ngRoute xeditable])

;; don't have to specify app name as compiler remember the last app name
;; defined in `defapp`
(defroute
  "/default" ['my-ctrl "partials/default.html"]
  "/profile/:profileId"
  {:controller 'profile-ctrl
   :template
   (hiccup
    [:div "{{fields}}"]
    [:table.table.table-bordered.table-hover.table-condensed
     [:tr {:style "font-weight: bold"}
      [:td {:style "width:35%"} "Name"]
      [:td {:style "width:35%"} "Value"]
      [:td {:style "width:30%"} "Edit"]]
     [:tr {:ng-repeat "field in fields"}
      [:td
       [:span {:editable-text "field.name"
               :e-name "name"
               :e-form "rowform"
               ;;:onbeforesave "check"
               :e-required ""}
        "{{ field.name || 'empty' }}"]]
      [:td
       [:span {:editable-text "field.value"
               :e-name "value"
               :e-form "rowform"
               ;;:onbeforesave "check"
               :e-required ""}
        "{{ field.value || 'empty' }}"]]
      [:td {:style "white-space: nowrap"}
       [:form.form-buttons.form-inline
        {:editable-form ""
         :name "rowform"
         ;; :onbeforesave "saveUser($data, user.id)"
         :ng-show "rowform.$visible"
         :shown "inserted == field"}
        [:button.btn.btn-primary
         {:type "submit" :ng-disabled "rowform.$waiting"}
         "Save"]
        [:button.btn.btn-default
         {:type "button" :ng-click "rowform.$cancel()"}
         "Cancel"]]
       [:div.buttons {:ng-show "!rowform.$visible"}
        [:button.btn.btn-primary
         {:ng-click "rowform.$show()"}
         "Edit"]
        [:button.btn.btn-danger
         {:ng-click "removeField($index)"}
         "Delete"]]]]]
    [:button.btn.btn-default
     {:ng-click "addField()"}
     "Add field"])}

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
       [:a.btn.btn-default
        {:ng-hide "textBtnForm.$visible"
         :href "{{ '#/profile/' + profile.id }}"} "Edit"]]]])}
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
  ($->atom profiles profiles))

(defcontroller profile-ctrl
  [$scope $routeParams]
  (def$ fields
    (-> profiles
        (find-entities {:id (parseInt (:profile-id $routeParams))})
        first
        (get :fields)))
  (defn$ remove-field [index]
    (.splice ($- fields)
             index 1))
  (defn$ add-field []
    (def$ fields
      (conj ($- fields)
            {:name "" :value ""}))))

(defcontroller empty-ctrl
  [$scope])

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
