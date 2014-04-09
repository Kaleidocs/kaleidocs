(ns kaleidocs.models
  (:use korma.db
        korma.core))

;;; Defines the database for lobos migrations
(def db-spec
  {:classname   "org.h2.Driver"
   :subprotocol "h2"
   :subname     "./lobos"})

(defdb clogdb db-spec)

(defentity posts)

(defentity authors
  (has-many posts :author))
