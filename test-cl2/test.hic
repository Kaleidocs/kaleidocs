[:html {:lang "en"}
 [:head
  [:meta  {:charset "utf-8"}]
  [:title "My App tests"]
  [:link {:rel "stylesheet" :href "/testem/qunit.css"}]
  [:script {:src "/testem/qunit.js"}]
  [:script {:src "/testem.js"}]
  [:script {:src "/vendor/angular/angular.js"}]
  [:script {:src "vendor/angular-route/angular-route.js"}]
  [:script {:src "/test-cl2/test_runner.js"}]]
 [:body {:ng-app "myApp"}
  [:div#qunit-userAgent]
  [:ol#qunit-tests]
  [:div#qunit-fixture]]]
