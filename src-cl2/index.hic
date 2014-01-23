[:html {:lang "en" :ng-app "myApp"}
 [:head
  [:meta  {:charset "utf-8"}]
  [:meta {:content "IE=edge", :http-equiv "X-UA-Compatible"}]
  [:meta
   {:content "width=device-width, initial-scale=1.0",
    :name "viewport"}]
  [:title "My App"]
  [:link {:rel "stylesheet"
          :href "vendor/bootstrap/dist/css/bootstrap.min.css"}]
  [:link {:rel "stylesheet"
          :href "vendor/angular-xeditable/dist/css/xeditable.css"}]
  [:link
   {:href
    "http://fonts.googleapis.com/css?family=Arimo|Open+Sans+Condensed:300|Noto+Serif:700italic&subset=latin"
    :rel "stylesheet"}]
  [:style
   "
   form[name='configForm'] > div {
     height: auto;
     padding: 5px 0;
   }

   form[name='configForm'] .title {
     display: inline-block;
     font-weight: bold;
     padding-top: 5px;
     vertical-align: top;
     min-width: 90px;
   }

   form[name='configForm'] .editable {
     display: inline-block;
     padding-top: 5px;
     vertical-align: top;
   }

   form[name='configForm'] select { width: 120px; }
   "]
  [:script
   "document.write('<base href=\"' + document.location + '\" />')"]
  [:script {:src "vendor/angular/angular.js"}]
  [:script {:src "vendor/angular-route/angular-route.js"}]
  [:script {:src "vendor/angular-xeditable/dist/js/xeditable.min.js"}]
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
     [:a.navbar-brand {:href "/"} "Kaleidocs"]]
    [:div.navbar-collapse.collapse
     [:ul.nav.navbar-nav
      [:li [:a {:href "#/config"} "Config"]]
      [:li [:a {:href "#/templates"} "Templates"]]
      [:li [:a {:href "#/profiles"} "Profiles"]]
      [:li [:a {:href "#/tables"} "Tables"]]
      [:li [:a {:href "#/produce"} "Produce"]]]]]
   [:div.container.row.span12
    [:div.span8.offset2
     [:ng-view]]]]]]
