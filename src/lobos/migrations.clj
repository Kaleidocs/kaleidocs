(ns lobos.migrations
  ;; exclude some clojure built-in symbols so we can use the lobos' symbols
  (:refer-clojure :exclude [alter drop complement
                            bigint boolean char double float time])
  (:require [lobos.core :refer [create drop alter]]
            [lobos.schema :refer [table integer bigint varchar]]
            [lobos.migration :refer [defmigration]]))

(defmigration add-document-table
  ;; code be executed when migrating the schema "up" using "migrate"
  (up [] (create (table :document
                        (integer :id :primary-key :auto-inc)
                        (varchar :filename 100 :unique
                                 [:collate :utf8-general-ci])
                        (varchar :fields 100
                                 [:collate :utf8-general-ci]))))
  (down [] (drop (table :document ))))

(defmigration add-docgroup-table
  (up [] (create (table :docgroup
                        (integer :id :primary-key :auto-inc)
                        (varchar :name 100 :unique
                                 [:collate :utf8-general-ci])
                        (varchar :documents 100
                                 [:collate :utf8-general-ci]))))
  (down [] (drop (table :docgroup))))

(defmigration add-profile-table
  (up [] (create (table :profile
                        (integer :id :primary-key :auto-inc))))
  (down [] (drop (table :profile))))

(defmigration add-contract-table
  (up [] (create (table :contract
                        (integer :id :primary-key :auto-inc)
                        (varchar :records 255
                                 [:collate :utf8-general-ci])
                        (varchar :date 100
                                 [:collate :utf8-general-ci])
                        (bigint :sum))))
  (down [] (drop (table :contract))))

(defmigration add-record-table
  (up [] (create (table :record
                        (integer :id :primary-key :auto-inc)
                        (varchar :date 100
                                 [:collate :utf8-general-ci])
                        (bigint :money)
                        ;; (integer :contract_id [:refer :contract :id])
                        (integer :docgroup_id [:refer :docgroup :id])
                        (integer :profile_id  [:refer :profile :id]))))
  (down [] (drop (table :record))))

(defmigration add-fields-table
  (up [] (create (table :custom_fields
                        (integer :id :primary-key :auto-inc)
                        (varchar :entity 100
                                 [:collate :utf8-general-ci])
                        (varchar :field 100 :unique
                                 [:collate :utf8-general-ci]))))
  (down [] (drop (table :custom_fields))))
