[:form.form-horizontal
 [:input {:value "{{p.id}}", :name "id", :type "hidden"}]
 [:div.form-group
  {:ng-repeat "key in ['fn', 'ln', 'em', 'dc', 'ph']"}
  [:label.col-sm-2.control-label "{{key}}"]
  [:input.w100 {:value "{{p[key]}}", :name "{{key}}", :type "text"}]]
 [:div.form-group
  [:div.col-sm-offset-2.col-sm-10
   [:input.btn.btn-default
    {:onclick "alert('Save :)')",
     :value " save ",
     :type "button"}]
   [:input.btn.btn-default
    {:ng-click "setEditId(-1)",
     :value "cancel",
     :type "button"}]]]]
