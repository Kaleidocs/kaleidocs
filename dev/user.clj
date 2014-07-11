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
   [http :refer [http-app]]
   [org.httpkit.server :refer [run-server]]
   [kaleidocs.core :refer [app]]))

(defn http-run [& args]
  (println "Server run @ 3003")
  (reset! http-app (run-server app {:port 3003})))

(def system
  "A Var containing an object representing the application under
  development."
  nil)

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
  (when-not (nil? @http-app)
    (@http-app)
    (println "Server stopped."))
  (reset! http-app nil)
  )

(defn reset
  "Stops the system, reloads modified source files, and restarts it."
  []
  (stop)
  (refresh-all)
  (start))
