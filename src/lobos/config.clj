(ns lobos.config
  (:require [lobos.connectivity :refer [open-global]])
  (:require [kaleidocs.models :refer [db-spec]]))

(open-global db-spec)
