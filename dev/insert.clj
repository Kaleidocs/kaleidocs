(ns insert
  (:use [kaleidocs.models]
        [korma.core]))

(defn insert-all []
  (insert
   authors
   (values [{:username "foo" :password "bla"
             :email "foo@bla.com"}
            {:username "fool" :password "bla"
             :email "fool@bla.com"}
            {:username "fu" :password "bla"
             :email "fu@bla.com"}]))
  (insert
   posts
   (values [{:title "writing" :content "Readme"
             :authors_id 1}])))

(defn select-all []
  (select authors (with posts)))
