(defcontroller history-ctrl
  [$scope]
  ($->atom records records)
  ($->atom history records
           (fn [x]
             (map #(merge (fields->map
                           (:fields (first (find-entities
                                            profiles
                                            {:id (:profile %)}))))
                          (fields->map (:fields %))
                          {:id (:id %)})
                  (vals x))))
  (def$ selection [])
  (def$ grid-options
    {:data "history"
     :multi-select false
     :show-filter true
     :selected-items ($- selection)
     :column-defs (map (fn [k] {:field k})
                       (:search-columns @config))}))
