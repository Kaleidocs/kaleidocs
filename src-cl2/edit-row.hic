[:form.form-horizontal
 [:input {:value "{{p.id}}", :name "id", :type "hidden"}]
 [:div.form-group
  {:ng-repeat "key in foreignKeys"}
  [:label.col-sm-2.control-label "{{key.foreignType}}"]
  [:input.w100
   {:name "{{key.foreignType}}",
    :type "number"
    :ng-model "p[key.foreignType+'_id']"
    :ng-controller "queryCtrl"
    :typeahead "entity.id as entity[key.foreignKey] for entity in
  findEntity(key.foreignType, key.foreignKey, $viewValue)"}]]
 [:div.form-group
  {:ng-repeat "key in itemKeys"}
  [:label.col-sm-2.control-label "{{key}}"]
  [:input.w100
   {:name "{{key}}",
    :type "{{key | fieldType }}"
    :ng-model "p[key]",}]]
 [:div.form-group
  [:div.col-sm-offset-2.col-sm-10
   [:input.btn.btn-default
    {:ng-click "saveItem(p); setEditId(-1)",
     :value "{{p.id ? 'save': 'add'}}",
     :type "button"}]
   [:input.btn.btn-default
    {:ng-click "setEditId(-1)",
     :value "cancel",
     :type "button"}]]]]
