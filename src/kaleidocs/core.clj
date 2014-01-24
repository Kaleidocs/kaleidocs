(ns kaleidocs.core
  (:require [methojure.sockjs.session :refer :all]
            [clojure.java.browse :refer [browse-url]]
            [simpledb.core :as db]
            [cheshire.core :refer [generate-string parse-string]]
            [clojure.string :as str]
            [taoensso.timbre :as timbre]
            [methojure.sockjs.core :refer :all]
            [compojure.core :refer [GET POST defroutes]]
            [org.httpkit.server :refer [run-server]]
            [ring.middleware.reload :refer [wrap-reload]]
            [ring.middleware.resource :refer [wrap-resource]]
            [ring.middleware.params :refer [wrap-params]])
  ;;(:gen-class)
  )

(db/init)

(defn dev? [args] (some #{"-dev"} args))

(defn port [args]
  (if-let [port (first (remove #{"-dev"} args))]
    (Integer/parseInt port)
    3000))

(def ^{:doc "Clients are stored in an atom as a hash-map with keys are
  client's id and values are Client records."}
  clients (atom {}))

(defrecord Client
    [session])

;; Newcomers are supplied with nick names in the form of a string
;; "Guest " followed by an unique number. A counter atom is used.
;; every time a new nick name is supplied, its value will be increased
;; by one.
;; Guests can change their names later.

(defn ->camelCase [^String method-name]
  (str/replace method-name #"-(\w)"
               #(str/upper-case (second %1))))

(defn generate-cl2-string
  "Converts Clojure maps to JSON-encoded ChlorineJs-friendly ones
  by camel-casing their keys."
  [data]
  (generate-string
   data
   {:key-fn (fn [k] (->camelCase (name k)))}))

(defn whisper
  "Sends messages to a single client"
  [id msg]
  (timbre/info "sending message to " id " " msg)
  (when-let [client-session (:session (get @clients id))]
    (send! client-session {:type :msg
                           :content
                           (generate-cl2-string msg)})))

(defn broadcast
  "Sends messages to many clients. An excluded client can be specified"
  [msg & ids-to-ignore]
  (timbre/info (str "Broadcasting "  msg
                    (when-let [ids ids-to-ignore]
                      (str " excluding "
                           (str/join ", " ids)))))
  (let [ignored-ids-set (set ids-to-ignore)]
    (doseq [[id client] @clients
            :when (not (contains? ignored-ids-set id))]
      (send! (:session client) {:type :msg
                                :content (generate-cl2-string msg)}))))

(defn on-init
  [id]
  (timbre/info "init message from " id)
  (whisper id
           ["counter" (db/get :counter)])
  (whisper id
           ["profiles" (db/get :profiles)])
  (whisper id
           ["config" (db/get :config)]))

(defn truncate
  "truncates a string to the given length"
  [^String s limit]
  (apply str (take limit s)))

(defn on-data
  "Handles messages when an on-data event happens"
  [msg-type data client-session]
  ;; TODO: max data size?
  (timbre/info "Data: " data
               " from session of " (:id client-session))
  (cond
   (= msg-type "init")
   (on-init (:id client-session))

   (= msg-type "config")
   (db/put! :config data)

   (= msg-type "profiles")
   (db/put! :profiles data)

   (= msg-type "counter")
   (db/put! :counter data)
))

(defrecord ChatConnection []
  SockjsConnection
  ;; on open is call whenever a new session is initiated.
  (on-open [this client-session]
    (let [id (:id client-session)]
      (timbre/info "New client connection: " id)
      (swap! clients
             assoc id (->Client client-session)))
    client-session)

  ;; on message is call when a new message arrives at the server.
  (on-message [this client-session raw-msg]
    (let [[msg-type data] (parse-string raw-msg true)]
      (on-data msg-type data client-session))
    client-session)

  ;; when a connection closes this method is called
  (on-close [this client-session]
    client-session))

(defroutes my-routes
  (POST "/upload" [] "hello world")
  (sockjs-handler
   "/socket" (->ChatConnection) {:response-limit 4096}))

(def app
  (-> my-routes
      (wrap-resource "public")
      (wrap-params)))

(defn -main [& args]
  (run-server
   (if (dev? args) (wrap-reload app) app)
   {:port (port args)})
  (browse-url (str "http://localhost:" (port args) "/index.html"))
  (timbre/info "server started on port" (port args)))
