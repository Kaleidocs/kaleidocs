(defcontroller history-ctrl
  [$scope]
  ($->atom records records)
  ($->atom history records
           (fn [x]
             (let [fields (map #(:fields %) (vals x))]
               (map fields->map fields))))
  (def$ grid-options
    {:data "history"
     :show-filter true
     :column-defs (map (fn [k] {:field k})
                       (:search-columns @config))}))
