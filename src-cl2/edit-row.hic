[:form.form-horizontal
 [:input {:value "{{p.id}}", :name "id", :type "hidden"}]
 [:div.form-group
  [:label.col-sm-2.control-label "Firstname:"]
  [:input.w100 {:value "{{p.fn}}", :name "fn", :type "text"}]]
 [:div.form-group
  [:label.col-sm-2.control-label "Email:"]
  [:input.w180 {:value "{{p.em}}", :name "em", :type "text"}]]
 [:div.form-group
  [:label.col-sm-2.control-label "Description:"]
  [:input.w180 {:value "{{p.ds}}", :name "ds", :type "text"}]]
 [:div.form-group
  [:label.col-sm-2.control-label "Lastname:"]
  [:input.w100 {:value "{{p.ln}}", :name "ln", :type "text"}]]
 [:div.form-group
  [:label.col-sm-2.control-label "Phone: "]
  [:input.w120 {:value "{{p.ph}}", :name "ph", :type "text"}]]
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
