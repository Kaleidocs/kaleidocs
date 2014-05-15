(ns kaleidocs.core
  (:require [noir.io :as io]
            [kaleidocs.models :refer :all]
            [noir.response :as response]
            [cheshire.core :refer [generate-string parse-string]]
            [clojure.string :as str]
            [taoensso.timbre :as timbre]
            [kaleidocs.generate :refer :all]
            [kaleidocs.convert :refer :all]
            [compojure.core :refer [GET POST PUT DELETE defroutes]]
            [org.httpkit.server :refer [run-server]]
            [ring.middleware.reload :refer [wrap-reload]]
            [ring.middleware.json :refer [wrap-json-body]]
            [ring.middleware.resource :refer [wrap-resource]]
            [ring.middleware.params :refer [wrap-params]]
            [ring.middleware.multipart-params :refer [wrap-multipart-params]])
  (:gen-class))

(defn dev? [args] (some #{"-dev"} args))

(defn port [args]
  (if-let [port (first (remove #{"-dev"} args))]
    (Integer/parseInt port)
    3000))

(defn find-order-key
  [s]
  (second (re-find #"sorting\[([a-zA-Z_]+)\]" s)))

(defn find-order-kv
  [params]
  (let [order-key (some find-order-key (keys params))
        order-value (if order-key
                      (case (get params (format "sorting[%s]" order-key))
                        "asc" :ASC
                        "desc" :DESC
                        nil)
                      :DESC)]
    [(or (keyword order-key) :id) order-value]))

(defn find-filter-key
  [s]
  (second (re-find #"filter\[([a-zA-Z_]+)\]" s)))

(defn find-filter-kvs
  [params]
  (let [filter-keys (remove nil? (map find-filter-key (keys params)))]
    (map (fn [k]
           [(keyword k)
            (get params (format "filter[%s]" k))])
         filter-keys)))

(defroutes my-routes
  (GET "/custom-fields" []
       (generate-string
        (fetch-custom-fields)))
  (GET "/" [] (response/redirect "/app.html"))
  (GET "/export" []
       (export-xls)
       "Export completed")
  (GET "/generate/:entity-type" [entity-type id]
       (case entity-type
         "record"
         (try (generate-records [(parse-int-or id)])
              {:status 200}
              (catch Throwable e
                (timbre/info "Error generating records" e)))
         "contract"
         (try (generate-contract (parse-int-or id))
              {:status 200}
              (catch Throwable e
                (timbre/info "Error generating contract" e)))))
  (GET "/records" [ids]
       (when (seq ids)
         (generate-string
          (query-records-by-ids (to-list ids)))))
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
  (POST   "/fields/:entity-type/:field"
          [entity-type field]
          (timbre/info "Adding field" field "in" entity-type)
          (add-custom-field entity-type field)
          (generate-string
           (fetch-custom-fields)))
  (PUT    "/fields/:entity-type/:old-field/:new-field"
          [entity-type old-field new-field]
          (timbre/info "Renaming field" old-field "to" new-field "in" entity-type)
          (rename-custom-field entity-type old-field new-field)
          (generate-string
           (fetch-custom-fields)))
  (DELETE "/fields/:entity-type/:field" [entity-type field]
          (timbre/info "Deleting field" field "in" entity-type)
          (remove-custom-field entity-type field)
          (generate-string
           (fetch-custom-fields)))
  (POST "/api/:entity-type" [entity-type :as {data :body}]
        (let [data (select-keys data
                                (allowed-columns entity-type))]
          (timbre/info "Got a post to" entity-type (pr-str data))
          (if (seq data)
            (do (if (integer? (:id data))
                  (update-entity entity-type (:id data) data)
                  (add-entity entity-type data))
                {:status 200})
            {:status 400
             :body "Empty data is not allowed"})))
  (DELETE ["/api/:entity-type/:id" :id #"[0-9]+"] [entity-type id]
          (timbre/info "Must delete this" entity-type id)
          (delete-entity entity-type id)
          {:status 200}))

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
