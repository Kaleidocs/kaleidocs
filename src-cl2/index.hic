[:html {:lang "en" :ng-app "myApp"}
 [:head
  [:meta  {:charset "utf-8"}]
  [:meta {:content "IE=edge", :http-equiv "X-UA-Compatible"}]
  [:meta
   {:content "width=device-width, initial-scale=1.0",
    :name "viewport"}]
  [:title "My App"]
  [:link {:rel "stylesheet" :href "vendor/bootstrap/dist/css/bootstrap.min.css"}]
  [:link
   {:href
    "http://fonts.googleapis.com/css?family=Arimo|Open+Sans+Condensed:300|Noto+Serif:700italic&subset=latin"
    :rel "stylesheet"}]
  [:script
   "document.write('<base href=\"' + document.location + '\" />')"]
  [:script {:src "vendor/angular/angular.js"}]
  [:script {:src "vendor/angular-route/angular-route.js"}]
  [:script {:src "core.js"}]]
 [:body
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
     [:a.navbar-brand {:href "/"} "My App"]]
    [:div.navbar-collapse.collapse
     [:ul.nav.navbar-nav
      [:li [:a {:href "#/default"} "Default view"]]
      [:li [:a {:href "#/view-2"} "View 2"]]]]]
   [:div.container.row.span12
    [:div.span8.offset2
     [:ng-view]]]]]]
