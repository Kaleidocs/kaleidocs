(ns kaleidocs.models
  (:require [korma.db :refer :all]
            [korma.core :refer :all]
            [korma.sql.fns :refer :all]
            [cheshire.core :refer [generate-string parse-string]]))

;;; Defines the database for lobos migrations
(def db-spec
  {:classname   "org.h2.Driver"
   :subprotocol "h2"
   :subname     "./lobos"})

(defdb clogdb db-spec)
(defn transform-data
  "Get v by query SQL. Transform it before sending out"
  [{data :data :as v}]
  (let [v (dissoc v :data)]
    (if (seq data)
      (merge v (parse-string data true))
      v)))

(defn prepare-data [& skipped-fields]
  (fn [{data :data :as v}]
    (let [data
          (apply dissoc v
                 skipped-fields)
          v (apply dissoc v (keys data))]
      (assoc v :data (generate-string data)))))


(defentity document)
(defentity docgroup)
(defentity profile
  (transform transform-data)
  (prepare (prepare-data :data :id)))
(defentity contract)
(defentity record
  (transform transform-data)
  (prepare (prepare-data
            :data :date :template :profile :contract :id :docgroup))
  (has-one docgroup)
  (has-one profile)
  (belongs-to contract))

(def name->entity
  (let [entity-types '[document docgroup profile contract record]]
    (zipmap (map name entity-types)
            (map eval entity-types))))

(defn delete-entity
  [entity-type id]
  (delete (name->entity entity-type)
          (where {:id [= id]})))

(defn add-entity
  [entity-type data]
  (insert (name->entity entity-type)
          (values data)))

(defn fetch-entities
  [entity-type page items-per-page
   order-key order-value
   filter-kvs]
  (let [where-clauses
        (when (seq filter-kvs)
          (map (fn [[k v]]
                 `(where* (pred-like ~k ~(str "%" v "%"))))
               filter-kvs))
        order-clauses
        (when order-key
          [`(order ~order-key ~order-value)])
        base
        (eval `(-> ~(name->entity entity-type)
                   select*
                   ~@where-clauses))]
    {:total (-> base
                (aggregate (count :*) :total)
                select first :total)
     :result (eval `(-> ~base
                        ~@order-clauses
                        (limit ~items-per-page)
                        (offset ~(* items-per-page (dec page)))
                        select))}))
