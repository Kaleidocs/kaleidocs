(ns user
  "Tools for interactive development with the REPL. This file should
  not be included in a production build of the application."
  (:require
   [clojure.java.io :as io]
   [clojure.java.javadoc :refer (javadoc)]
   [clojure.pprint :refer (pprint)]
   [clojure.reflect :refer (reflect)]
   [clojure.repl :refer (apropos dir doc find-doc pst source)]
   [clojure.set :as set]
   [clojure.string :as str]
   [clojure.test :as test]
   [clojure.tools.namespace.repl :refer (refresh refresh-all)]
   [creationism :refer :all]
   [insert :refer :all]
   [org.httpkit.server :refer [run-server]]
   [kaleidocs.core :refer [app]]))

(def http-app (atom nil))

(defn http-run [& args]
  (println "Server run @ 3003")
  (reset! http-app (run-server app {:port 3003})))

(def system
  "A Var containing an object representing the application under
  development."
  nil)

(defn init
  "Creates and initializes the system under development in the Var
  #'system."
  []
  (create-all)
  )

(defn start
  "Starts the system running, updates the Var #'system."
  []
  (if-not @http-app
    (http-run))
  )

(defn stop
  "Stops the system if it is currently running, updates the Var
  #'system."
  []
  (if (fn? @http-app)
    (@http-app))
  )

(defn go
  "Initializes and starts the system running."
  []
  (init)
  (start)
  :ready)

(defn reset
  "Stops the system, reloads modified source files, and restarts it."
  []
  (stop)
  (destroy-all)
  (refresh :after 'user/go))
