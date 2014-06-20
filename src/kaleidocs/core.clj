(ns kaleidocs.core
  (:require [noir.io :as io]
            [kaleidocs.models :refer :all]
            [kaleidocs.utils :refer [find-order-kv find-filter-kvs
                                     file-response-as
                                     normalize-diacritics-map]]
            [noir.response :as response]
            [cheshire.core :refer [generate-string parse-string]]
            [clojure.string :as str]
            [taoensso.timbre :as timbre]
            [kaleidocs.generate :refer :all]
            [kaleidocs.convert :refer :all]
            [compojure.core :refer [GET POST PUT DELETE defroutes context]]
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

(defn custom-fields-json []
  (generate-string
   (merge {"profile"  []
           "record"   []
           "contract" []}
          (fetch-custom-fields))))

(defroutes fields-route
  (GET    "/" []
          (custom-fields-json))
  (POST   "/:entity-type/:field"
          [entity-type field]
          (timbre/info "Adding field" field "in" entity-type)
          (add-custom-field entity-type field)
          (custom-fields-json))
  (PUT    "/:entity-type/:old-field/:new-field"
          [entity-type old-field new-field]
          (timbre/info "Renaming field" old-field "to" new-field "in" entity-type)
          (rename-custom-field entity-type old-field new-field)
          (custom-fields-json))
  (DELETE "/:entity-type/:field" [entity-type field]
          (timbre/info "Deleting field" field "in" entity-type)
          (remove-custom-field entity-type field)
          (custom-fields-json)))

(defroutes api-route
  (GET "/:entity-type" [entity-type page count & other-params]
       (let [[order-key order-value] (find-order-kv other-params)
             filter-kvs (find-filter-kvs other-params)
             page (parse-int page 1)
             count (parse-int count 10)]
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
  (POST "/:entity-type" [entity-type :as {data :body}]
        (let [data (-> data
                       (select-keys (allowed-columns entity-type))
                       normalize-diacritics-map)]
          (timbre/info "Got a post to" entity-type (pr-str data))
          (if (seq data)
            (do (if (integer? (:id data))
                  (update-entity entity-type (:id data) data)
                  (add-entity entity-type data))
                {:status 200})
            {:status 400
             :body "Empty data is not allowed"})))
  (DELETE ["/:entity-type/:id" :id #"[0-9]+"] [entity-type id]
          (timbre/info "Must delete this" entity-type id)
          (delete-entity entity-type id)
          {:status 200}))

(defroutes my-routes
  (GET "/" [] (response/redirect "/app.html"))
  (context "/fields" [] fields-route)
  (context "/api"    [] api-route)
  (GET "/export" []
       (export-xls)
       (file-response-as "output/export.xls" "export.xls"))
  (GET "/download/:filename" [filename]
       (file-response-as (str "output/" filename) filename))
  (GET "/generate/:entity-type/:id" [entity-type id]
       (case entity-type
         "record"
         (try (-> id parse-int vector generate-records generate-string)
              (catch Throwable e
                (timbre/info "Error generating records" e)))
         "contract"
         (try (-> id parse-int generate-contract generate-string)
              (catch Throwable e
                (timbre/info "Error generating contract" e)))))
  (GET "/records" [ids]
       (when (seq ids)
         (generate-string
          (query-records-by-ids (split-string->list ids))))))

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
