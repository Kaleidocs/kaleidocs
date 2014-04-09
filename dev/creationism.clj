(ns creationism
  (:use [lobos.core]
        [kaleidocs.models :only [db-spec]]
        [lobos.connectivity]
        [lobos.migration]
        [lobos.migrations]))

(defn create-all []
  (open-global db-spec)
  (migrate)
  (close-global))

(defn destroy-all []
  (open-global db-spec)
  (rollback :all)
  (close-global))
