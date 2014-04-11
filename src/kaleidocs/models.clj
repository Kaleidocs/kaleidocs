(ns kaleidocs.models
  (:use korma.db
        korma.core))

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
