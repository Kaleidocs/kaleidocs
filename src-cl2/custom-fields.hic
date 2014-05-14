[:h2 "Custom fields"]
[:form.form-horizontal {:ng-repeat "
(entity, fieldNames) in fields"}
 [:h3 "{{entity}}"]
 [:div.form-group {:ng-repeat "
  field in fieldNames"}
  [:div.col-sm-6
   [:input.form-control
    {:ng-model "field"}]]
  [:button.btn.btn-xs.btn-warning
   {:ng-click "renameField(entity, field)"}
   "Rename"]
  [:button.btn.btn-xs.btn-danger
   {:ng-click "removeField(entity, field)"}
   "Delete"]]
 [:div.form-group
  [:div.col-sm-6
   [:input.form-control
    {:ng-model "field"}]]
  [:button.btn.btn-success
   {:ng-click "addField(entity, field)"}
   "Add"]]]
