[:html {:lang "en" :ng-app "myApp"}
 [:head
  [:meta  {:charset "utf-8"}]
  [:meta {:content "IE=edge", :http-equiv "X-UA-Compatible"}]
  [:meta
   {:content "width=device-width, initial-scale=1.0",
    :name "viewport"}]
  [:title "Kaleidocs"]
  [:link {:rel "stylesheet"
          :href "vendor/bootstrap/dist/css/bootstrap.min.css"}]
  [:link {:rel "stylesheet"
          :href "vendor/ng-table/ng-table.css"}]
  [:link {:rel "stylesheet"
          :href "app.css"}]
  [:script
   "document.write('<base href=\"' + document.location + '\" />')"]
  [:script {:src "vendor/angular/angular.js"}]
  [:script {:src "vendor/angular-sanitize/angular-sanitize.min.js"}]
  [:script {:src "vendor/angular-animate/angular-animate.min.js"}]
  [:script {:src "vendor/angular-i18n/angular-locale_vi-vn.js"}]
  [:script {:src "vendor/angular-route/angular-route.js"}]
  [:script {:src "vendor/angular-resource/angular-resource.js"}]
  [:script {:src "vendor/ng-table/ng-table.js"}]
  [:script {:src "vendor/angular-bootstrap/ui-bootstrap-tpls.js"}]
  [:script {:src "vendor/ng-file-upload/angular-file-upload.min.js"}]
  [:script {:src "app.js"}]]
 [:body {:ng-cloak ""}
  [:div.container
   [:div.navbar.navbar-default
    {:role "navigation"}
    [:div.navbar-header
     [:button.navbar-toggle
      {:data-target ".navbar-collapse",
       :data-toggle "collapse",
       :type "button"}
      [:span.sr-only "Toggle navigation"]
      [:span.icon-bar]
      [:span.icon-bar]
      [:span.icon-bar]]
     [:a.navbar-brand {:href "/"}
      [:span.glyphicon.glyphicon-home ""] "    Kaleidocs    "]]
    [:div.navbar-collapse.collapse
     [:ul.nav.navbar-nav {:ng-controller "navbarCtrl"}
      [:li {:ng-repeat "entity in
['profile', 'record', 'contract']"
            :class
"{{( '/'+entity === $location.path() ) ? 'active' : '' }}"}
       [:a {:href "#/{{entity}}"}
        [:span.glyphicon.glyphicon-bookmark " "]
        "    {{entity}}"]]
      [:li [:a {:href "/export"}
            [:span.glyphicon.glyphicon-file " "]
            "     Export"]]]
     [:ul.nav.navbar-nav.navbar-right
      [:li [:a {:href "https://github.com/Kaleidocs/kaleidocs/wiki"}
            [:span.glyphicon.glyphicon-question-sign " "]
            "     About"]]]]]
   [:div.container.row.span12
    [:div.span8.offset2
     [:div {:ng-controller "alertsCtrl"}
      [:button.btn.btn-default
       {:ng-show "(alerts.length > 0)"
        :ng-click "clearAlerts()"}
       "Clear alerts"]
      [:alert
       {:close "removeAlert(alert.id)",
        :type "alert.type",
        :ng-repeat "alert in alerts"}
       [:p "{{alert.msg}}"]
       [:a.btn.btn-default {:ng-repeat "file in alert.files"
            :href "/download/{{file}}"}
        [:span.glyphicon.glyphicon-download-alt " "]
        "{{file}}"]]]
     [:ng-view]]]]]]
