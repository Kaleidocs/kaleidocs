(defcontroller produce-ctrl
  [$scope]
  ($->atom produce produce)
  (defn$ export-table [k1 k2 v1 v2]
    (zipmap [k1 k2] [v1 v2]))
  (defn$ get-records [ids]
    (find-entities records #(contains? (set ids) (:id %))))
  (defn$ get-profile [records]
    (first (find-entities profiles {:id (:profile (first records))})))
  )
