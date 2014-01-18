(load-file "angular-cl2/src/core.cl2")

(defapp my-app [ngRoute])

;; don't have to specify app name as compiler remember the last app name
;; defined in `defapp`
(defroute
  "/default" ['my-ctrl "partials/default.html" ]
  "/view-2"  {:controller 'empty-ctrl
              :template
              (hiccup
               [:h2
                "Hiccup rocks!"])}
  :default "/default")

(defdirective my-directive
  []
  ;; can be a directive-definition object or a link function
  (fn [scope elm attrs]
    (.
     scope
     ($watch
      (:my-directive attrs)
      (fn [value] (. elm (text (+ value 4))))))))

(defcontroller my-ctrl
  [$scope my-service]
  (def$ some-number 12)
  (defn$ add-two [n] {:result (+ n 2)})
  (defn$ service-add [n]
    (my-service.add-three n)))

(defcontroller empty-ctrl
  [$scope])

;; example of specifying app name
(defservice my-app my-service
 []
 (this->!)
 (defn! add-three [n] (+ n 3)))

;; generic defmodule usage
(defmodule my-app
  (:filter (my-filter [] [s] (+ s 5))))

(deffilter your-filter []
  [s]
  (+ s 6))
