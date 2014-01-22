[:div {:ng-repeat "profile in profiles"}
 [:h3
  [:a {:editable-text "profile.name"}
   "{{ profile.name || 'empty' }}"]]
 [:a.btn.btn-warning.btn-xs
  {:ng-hide "textBtnForm.$visible"
   :href "{{ '#/profile/' + profile.id }}"}
  [:span.glyphicon.glyphicon-pencil] "Edit"]
 [:button.btn.btn-danger.btn-xs
  {:ng-click "removeProfile(profile.id)"
   :ng-hide "textBtnForm.$visible"}
  [:span.glyphicon.glyphicon-remove-circle]
  "Delete"]]
[:br]
[:button.btn.btn-success.btn-lg
 {:ng-click "addProfile()"}
 [:span.glyphicon.glyphicon-plus-sign]
 "Add profile"]
