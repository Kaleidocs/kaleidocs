(ns kaleidocs.models
  (:require [korma.db :refer :all]
            [korma.core :refer :all]
            [korma.sql.fns :refer :all]
            [clj-excel.core :refer [build-workbook workbook-hssf save]]
            [kaleidocs.convert :refer [output-dir multi-doc?]]
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

(def db-spec
  {:classname   "org.h2.Driver"
   :subprotocol "h2"
   :subname     "./data;IGNORECASE=TRUE"})

(declare fetch-custom-fields)

(def base-columns
  {"document" [:id :filename :fields]
   "docgroup" [:id :name :documents]
   "contract" [:id :records :date :sum]
   "profile"  [:id]
   "record"   [:id :date :money :docgroup_id :profile_id]})

(defdb clogdb db-spec)

(defentity document)
(defentity docgroup)
(defentity profile)
(defentity contract)
(defentity custom_fields)

(defentity record
  (belongs-to docgroup)
  (belongs-to profile))

(defn allowed-columns [entity-type]
  (into (base-columns entity-type)
        (when (#{"profile" "record"} entity-type)
          (->> (select custom_fields
                       (where {:entity "record"}))
               (map #(-> % :field keyword))))))

(defn exported-columns [entity-type]
  (if (= "record" entity-type)
    (into (base-columns entity-type)
          (->> (select custom_fields
                       (where (or {:entity "record"}
                                  {:entity "profile"})))
               (map #(-> % :field keyword))))
    (allowed-columns entity-type)))

(def name->entity
  (let [entity-types '[document docgroup profile record contract]]
    (zipmap (map name entity-types)
            (map eval entity-types))))

(def name->entity-base*
  (let [entity-types '[document docgroup profile contract]]
    (zipmap (map name entity-types)
            (map #(select* (eval %)) entity-types))))

(def name->entity-base
  (assoc name->entity-base*
    "record"
    (-> record
        select*
        (with docgroup)
        (with profile))))

(defn fetch-records [ids]
  (select (name->entity-base "record")
          (where {:id [in ids]})))

(defn fetch-contract [id]
  (-> contract
      select*
      (where {:id [pred-= id]})
      select first))

(defn transform-fields [m]
  (assoc m :fields
         (clojure.string/split (:fields m) #",")))

(defn fetch-multidocs []
  (filter multi-doc? (select document)))

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
  (let [base (-> (name->entity-base "record")
                 (where {:id [in ids]}))]
    {:sum (-> base
              (aggregate (sum :money) :total)
              select first :total)
     :result (-> base select)}))

(defn fetch-custom-fields []
  (->> (select custom_fields)
       (group-by :entity)
       ((fn [coll] (zipmap (keys coll)
                           (map #(map :field %) (vals coll)))))))

(defn add-column [entity field]
  (exec-raw (format "ALTER TABLE \"%s\" ADD \"%s\" varchar(100)"
                    entity field)))

(defn drop-column [entity field]
  (exec-raw (format "ALTER TABLE \"%s\" DROP COLUMN \"%s\""
                    entity field)))

(defn add-custom-field [entity field]
  (transaction
   (insert custom_fields
           (values {:entity entity :field field}))
   (add-column entity field)))

(defn remove-custom-field [entity field]
  (transaction
   (delete custom_fields
           (where {:entity entity :field field}))
   (drop-column entity field)))

(defn fetch-entities
  [entity-type page items-per-page
   order-key order-value
   filter-kvs]
  (let [with-where-clauses
        (if (seq filter-kvs)
          (->> filter-kvs
               (map
                (fn [[k v]]
                  #(where* % (pred-like k (str "%" (url-decode v) "%")))))
               (apply comp))
          identity)
        base
        (with-where-clauses (name->entity-base entity-type))
        base-with-order
        (if order-key
          (-> base
              (order order-key order-value))
          base)]
    {:total (-> base
                (aggregate (count :*) :total)
                select first :total)
     :result (-> base-with-order
                 (limit items-per-page)
                 (offset (* items-per-page (dec page)))
                 select)}))

(defn as-row [m columns]
  (map m columns))

(defn get-all-data []
  (into {} (for [k (keys base-columns)
                 :let [v (exported-columns k)]]
             [k (cons (map name v) ;; header row
                      (map #(as-row % v) (select (name->entity-base k))))])))

(defn export-xls []
  (-> (workbook-hssf)
      (build-workbook (get-all-data))
      (save (str output-dir "/" "export.xls"))))
