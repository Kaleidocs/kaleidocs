(ns kaleidocs.models
  (:require [korma.db :refer :all]
            [korma.core :refer :all]
            [korma.sql.fns :refer :all]
            [ring.util.codec :refer [url-decode]]
            [cheshire.core :refer [generate-string parse-string]]))

(defn parse-int-or [s & [default]]
  (try (Integer/parseInt s)
       (catch Throwable e default)))

(defn to-list [s]
  (->> #","
       (clojure.string/split s)
       (map parse-int-or )
       (remove nil?)))

;;; Defines the database for lobos migrations
(def db-spec
  {:classname   "org.h2.Driver"
   :subprotocol "h2"
   :subname     "./lobos"})

(def allowed-columns
  {"document" [:id :filename :fields]
   "docgroup" [:id :name :documents]
   "profile"  [:id :company :bank :account :city]
   "contract" [:id :records :date :sum]
   "record"   [:id :date :money :remarks
               :contract_id :docgroup_id :profile_id]})

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
(defentity profile)
(defentity contract)

;; Don't declare relationship to avoid
;; java.lang.RuntimeException: Can't embed object in code, maybe print-dup not defined
(defentity record)

(defentity expanded-record
  (table :record)
  (belongs-to docgroup)
  (belongs-to profile))

(defn fetch-expanded-records [ids]
  (select expanded-record
          (where {:id [in ids]})
          (with docgroup
                (fields :documents))
          (with profile
                (fields :company :bank :account :city))))

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

(defn update-entity
  [entity-type id data]
  (update (name->entity entity-type)
          (set-fields data)
          (where {:id [= id]})))

(defn query-records-by-ids [ids]
  (let [base (-> record
                 select*
                 (where {:id [in ids]}))]
    {:sum (-> base
              (aggregate (sum :money) :total)
              select first :total)
     :result (-> base select)}))

(defn fetch-entities
  [entity-type page items-per-page
   order-key order-value
   filter-kvs]
  (let [where-clauses
        (when (seq filter-kvs)
          (map (fn [[k v]]
                 `(where* (pred-like ~k ~(str "%" (url-decode v) "%"))))
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
