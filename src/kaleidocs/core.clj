(ns kaleidocs.core
  (:require [methojure.sockjs.session :refer :all]
            [clojure.java.browse :refer [browse-url]]
            [noir.io :as io]
            [kaleidocs.merge :refer [merge-doc]]
            [kaleidocs.models :refer :all]
            [noir.response :as response]
            [cheshire.core :refer [generate-string parse-string]]
            [clojure.string :as str]
            [taoensso.timbre :as timbre]
            [kaleidocs.convert :refer :all]
            [methojure.sockjs.core :refer :all]
            [compojure.core :refer [GET POST DELETE defroutes]]
            [org.httpkit.server :refer [run-server]]
            [ring.middleware.reload :refer [wrap-reload]]
            [ring.middleware.json :refer [wrap-json-body]]
            [ring.middleware.resource :refer [wrap-resource]]
            [ring.middleware.params :refer [wrap-params]]
            [ring.middleware.multipart-params :refer [wrap-multipart-params]])
  (:gen-class))

(def templates-dir "templates")
(def output-dir "output")

(declare broadcast)

(defn odf-template [s]
  (str templates-dir "/" (odf-filename s)))

(defn gen-doc [single-templates multiple-templates table-keys
               records data-map]
  (doseq [t multiple-templates
          :let
          [extension
           (filename->extension t)
           generated-odf
           (str output-dir "/" (get data-map "PID") "_" (odf-filename t))]]
    (broadcast [:status (str "Merging " generated-odf)])
    (merge-doc (odf-template t)
               generated-odf
               (map #(str "TABLE." %) table-keys)
               (merge data-map {"TABLE" records}))
    (when (mso-file? t)
      (broadcast [:status (str "Exporting " generated-odf
                               " to " extension)])
      (convert-doc extension generated-odf output-dir)
      (.delete (clojure.java.io/file generated-odf)))
    (broadcast [:status (str "Finished " t)]))

  (doseq [t single-templates
          r records
          :let
          [extension
           (filename->extension t)
           generated-odf
           (str output-dir "/" (get r "id") "_" (odf-filename t))]]
    (broadcast [:status (str "Merging " t)])
    (merge-doc (odf-template t)
               generated-odf
               (merge data-map r))
    (when (mso-file? t)
      (broadcast [:status (str "Exporting " generated-odf
                               " to " extension)])
      (convert-doc extension generated-odf output-dir)
      (.delete (clojure.java.io/file generated-odf)))
    (broadcast [:status (str "Finished " t)]))

  (broadcast [:status (format "Producing documents #%s finished!"
                              (get data-map "PID"))]))

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
  [[msg-type data]]
  (generate-string
   [(->camelCase (name msg-type)) data]
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
  (doseq [data-type [:templates :counter :profiles :config :records]]
    (whisper id
             [data-type "todo"])))

(defn truncate
  "truncates a string to the given length"
  [^String s limit]
  (apply str (take limit s)))

(defn on-data
  "Handles messages when an on-data event happens"
  [msg-type data client-session]
  ;; TODO: max data size?
  (timbre/info "Data: " data "with type " msg-type
               " from session " (:id client-session))
  (cond
   (= msg-type "init")
   (on-init (:id client-session))

   (contains? #{"config" "profiles" "counter" "templates" "records"}
              msg-type)
   "todo"

   (= msg-type "deleteFile")
   (do (.delete (clojure.java.io/file (odf-template data)))
       (broadcast [:status (format "Template %s deleted." data)]))

   (= msg-type "genDoc")
   (apply gen-doc data)
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
    (let [[msg-type data] (parse-string raw-msg)]
      (on-data msg-type data client-session))
    client-session)

  ;; when a connection closes this method is called
  (on-close [this client-session]
    client-session))

(declare testbed-data)

(defn find-order-key
  [s]
  (second (re-find #"sorting\[([a-zA-Z]+)\]" s)))

(defn find-order-kv
  [params]
  (let [order-key (some find-order-key (keys params))
        order-value (when order-key
                      (case (get params (format "sorting[%s]" order-key))
                        "asc" :ASC
                        "desc" :DESC
                        nil))]
    [(keyword order-key) order-value]))

(defn find-filter-key
  [s]
  (second (re-find #"filter\[([a-zA-Z]+)\]" s)))

(defn find-filter-kvs
  [params]
  (let [filter-keys (remove nil? (map find-filter-key (keys params)))]
    (map (fn [k]
           [(keyword k)
            (get params (format "filter[%s]" k))])
         filter-keys)))

(defn parse-int-or [s & [default]]
  (try (Integer/parseInt s)
       (catch Throwable e default)))

(defn to-list [s]
  (->> #","
       (clojure.string/split s)
       (map parse-int-or )
       (remove nil?)))

(defroutes my-routes
  (GET "/api/:entity-type" [entity-type page count & other-params]
       (let [[order-key order-value] (find-order-kv other-params)
             filter-kvs (find-filter-kvs other-params)
             page (parse-int-or page 1)
             count (parse-int-or count 10)]
         (do (timbre/info
              (format "type: %s; page %s; count %s"
                      entity-type page count))
             (timbre/info
              (format "order-key %s and order-value %s"
                      order-key order-value))
             (timbre/info
              (format "filter-kvs %s"
                      (pr-str filter-kvs))))
         (generate-string
          (fetch-entities
           entity-type page count
           order-key order-value
           filter-kvs))))
  (POST "/api/:entity-type" [entity-type :as {data :body}]
        (let []
          (timbre/info "Got a post to" entity-type (pr-str data))
          (if (integer? (:id data))
            (update-entity entity-type (:id data) data)
            (add-entity entity-type data))
          {:status 200}))
  (DELETE ["/api/:entity-type/:id" :id #"[0-9]+"] [entity-type id]
          (timbre/info "Must delete this" entity-type id)
          (delete-entity entity-type id)
          {:status 200})
  (POST "/upload" [file]
        ;; if file exists, just overwrite with new one
        ;; else, inform clients about new template
        (let [filename (:filename file)
              odf-template (str templates-dir "/"
                                (odf-filename filename))]
          (if-not (or (mso-file? filename)
                      (odf-file? filename))
            (broadcast [:status "Error: Unknown file format"])
            (do
              (if (.exists (clojure.java.io/file odf-template))
                (broadcast [:status (str "Updating template " filename)])
                (broadcast [:new-template {:filename filename}]))
              ;; copy to templates dir

              (io/upload-file templates-dir file :create-path? true)

              (when (mso-file? filename)
                (broadcast [:status (str "Importing template " filename)])
                (let [mso-template (str templates-dir "/" filename)]
                  ;; convert to odf
                  (convert-doc (filename->target-format filename)
                               mso-template
                               templates-dir)
                  (.delete (clojure.java.io/file mso-template)))
                (broadcast [:status
                            (str "Importing template " filename " finished")]))))
          filename))
  (sockjs-handler
   "/socket" (->ChatConnection) {:response-limit 4096}))

(def app
  (-> my-routes
      (wrap-resource "public")
      wrap-params
      (wrap-json-body {:keywords? true :bigdecimals? true})
      wrap-multipart-params))

(defn -main [& args]
  (run-server
   (if (dev? args) (wrap-reload app) app)
   {:port (port args)})
  (timbre/info "server started on port" (port args)))

(def testbed-data
  [{"dl" false,
    "ac" true,
    "ph" "617-321-4567",
    "em" "j.smith@company.com",
    "dc" "CEO",
    "fn" "John",
    "ln" "Smith",
    "lm" 138661285100,
    "id" 1}
   {"dl" false,
    "ac" true,
    "ph" "617-522-5588",
    "em" "l.taylor@company.com",
    "dc" "VP of Marketing",
    "fn" "Lisa",
    "ln" "Taylor",
    "lm" 138661285200,
    "id" 2}
   {"dl" false,
    "ac" true,
    "ph" "617-589-9977",
    "em" "j.jones@company.com",
    "dc" "VP of Sales",
    "fn" "James",
    "ln" "Jones",
    "lm" 138661285300,
    "id" 3}
   {"dl" false,
    "ac" true,
    "ph" "617-245-9785",
    "em" "p.wong@company.com",
    "dc" "VP of Engineering",
    "fn" "Paul",
    "ln" "Wong",
    "lm" 138661285400,
    "id" 4}
   {"dl" false,
    "ac" true,
    "ph" "617-244-1177",
    "em" "a.king@company.com",
    "dc" "Architect",
    "fn" "Alice",
    "ln" "King",
    "lm" 138661285500,
    "id" 5}
   {"dl" false,
    "ac" true,
    "ph" "617-568-9863",
    "em" "j.brown@company.com",
    "dc" "Software Engineer",
    "fn" "Jan",
    "ln" "Brown",
    "lm" 138661285600,
    "id" 6}
   {"dl" false,
    "ac" true,
    "ph" "617-327-9966",
    "em" "a.garcia@company.com",
    "dc" "Software Engineer",
    "fn" "Ami",
    "ln" "Garcia",
    "lm" 138661285700,
    "id" 7}
   {"dl" false,
    "ac" true,
    "ph" "617-565-9966",
    "em" "j.green@company.com",
    "dc" "Software Engineer",
    "fn" "Jack",
    "ln" "Green",
    "lm" 138661285800,
    "id" 8}
   {"dl" false,
    "ac" true,
    "ph" "617-523-4468",
    "em" "a.liesen@company.com",
    "dc" "Plumber",
    "fn" "Abraham",
    "ln" "Liesen",
    "lm" 138661285900,
    "id" 9}
   {"dl" false,
    "ac" true,
    "ph" "617-877-3434",
    "em" "a.bower@company.com",
    "dc" "Product Manager",
    "fn" "Angela",
    "ln" "Bower",
    "lm" 138661286000,
    "id" 10}
   {"dl" false,
    "ac" true,
    "ph" "617-446-9999",
    "em" "f.davidoff@company.com",
    "dc" "Database Admin",
    "fn" "Fjodor",
    "ln" "Davidoff",
    "lm" 138661286100,
    "id" 11}
   {"dl" false,
    "ac" true,
    "ph" "617-111-1111",
    "em" "b.vitrovic@company.com",
    "dc" "Director of Communications",
    "fn" "Biljana",
    "ln" "Vitrovic",
    "lm" 138661286200,
    "id" 12}
   {"dl" false,
    "ac" true,
    "ph" "617-565-4412",
    "em" "g.valet@company.com",
    "dc" "Software Engineer",
    "fn" "Guillaume",
    "ln" "Valet",
    "lm" 138661286300,
    "id" 13}
   {"dl" false,
    "ac" true,
    "ph" "617-866-2554",
    "em" "m.tran@company.com",
    "dc" "Gui Designer",
    "fn" "Min",
    "ln" "Tran",
    "lm" 138661286400,
    "id" 14}])
