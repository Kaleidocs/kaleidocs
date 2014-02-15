[:div {:ng-repeat "profile in profiles"}
 [:button.btn.btn-danger.btn-xs.pull-right
  {:ng-click "removeProfile(profile.id)"
   :ng-hide "textBtnForm.$visible"}
  [:span.glyphicon.glyphicon-remove-circle]
  "Delete"]
 [:button.btn.btn-success.btn-xs.pull-right
  {:ng-click "profileShow = !profileShow"
   :ng-show "!profileShow"}
  [:span.glyphicon.glyphicon-eye-open]
  "Show"]
 [:button.btn.btn-success.btn-xs.pull-right
  {:ng-click "profileShow = !profileShow"
   :ng-show "profileShow"}
  [:span.glyphicon.glyphicon-eye-close]
  "Hide"]
 [:h3
  [:a {:editable-text "profile.name"}
   "{{ profile.name || 'empty' }}"]]
 [:div {:ng-include "'partials/profile.html'"
        :ng-controller "profileCtrl"}]]
[:br]
[:button.btn.btn-success.btn-lg
 {:ng-click "addProfile()"}
 [:span.glyphicon.glyphicon-plus-sign]
 "Add profile"]
