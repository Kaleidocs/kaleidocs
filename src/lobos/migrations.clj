(ns lobos.migrations
  ;; exclude some clojure built-in symbols so we can use the lobos' symbols
  (:refer-clojure :exclude [alter drop complement
                            bigint boolean char double float time])
  ;; use only defmigration macro from lobos
  (:use (lobos [migration :only [defmigration]]
               core
               schema)
        [kaleidocs.models :only [db-spec]]))

(defmigration add-document-table
  ;; code be executed when migrating the schema "up" using "migrate"
  (up [] (create db-spec
                 (table :document
                        (integer :id :primary-key :auto-inc)
                        (varchar :filename 100 :unique)
                        (varchar :fields 100))))
  (down [] (drop (table :document ))))

(defmigration add-docgroup-table
  (up [] (create db-spec
                 (table :docgroup
                        (integer :id :primary-key :auto-inc)
                        (varchar :name 100 :unique)
                        (varchar :documents 100))))
  (down [] (drop (table :docgroup))))

(defmigration add-profile-table
  (up [] (create db-spec
                 (table :profile
                        (integer :id :primary-key :auto-inc)
                        (varchar :company 100 :unique)
                        (varchar :bank 100)
                        (varchar :account 100)
                        (varchar :city 100))))
  (down [] (drop (table :profile))))

(defmigration add-contract-table
  (up [] (create db-spec
                 (table :contract
                        (integer :id :primary-key :auto-inc)
                        (varchar :records 255)
                        (varchar :date 100)
                        (integer :sum))))
  (down [] (drop (table :contract))))

(defmigration add-record-table
  (up [] (create db-spec
                 (table :record
                        (integer :id :primary-key :auto-inc)
                        (varchar :date 100)

                        (integer :money)
                        (varchar :remarks 100)
                        (varchar :contract 100)
                        (integer :docgroup [:refer :docgroup :id])
                        (integer :profile [:refer :profile :id]))))
  (down [] (drop (table :record))))
