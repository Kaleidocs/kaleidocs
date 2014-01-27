(defcontroller produce-ctrl
  [$scope]
  ($->atom produce produce)
  (defn$ gen-doc [& data]
    (. socket emit :gen-doc data))
  (defn$ get-single-templates []
    (map #(:filename %) (find-entities templates {:type "single"})))
  (defn$ get-multiple-templates []
    (map #(:filename %) (find-entities templates {:type "multiple"})))
  (def$ export-records export-records)
  (defn$ pr-str [x]
    (pr-str x))
  (defn$ fields->map [fields]
    (fields->map fields))
  (defn$ export-table [k1 k2 v1 v2]
    (zipmap [k1 k2] [v1 v2]))
  (defn$ get-records [ids]
    (find-entities records #(contains? (set ids) (:id %))))
  (defn$ get-profile [records]
    (first (find-entities profiles {:id (:profile (first records))})))
  )
