(defcontroller history-ctrl
  [$scope]
  ($->atom records records)
  ($->atom history records
           (fn [x]
             (map #(merge (fields->map
                           (:fields (first (find-entities
                                            profiles
                                            {:id (:profile %)}))))
                          (format-amount&add-iw!
                           (fields->map (:fields %)))
                          {:id (:id %)
                           :contract (.join (:contract %) ",")})
                  (vals x))))
  (defn$ gen-csv []
    (map #(merge (columns->empty-map (:export-columns @config))
                 (select-keys % (:export-columns @config)))
         ($- history)))
  ($->atom csv-header config #(:export-columns %))
  (def$ selection [])
  (def$ grid-options
    {:data "history"
     :multi-select false
     :enable-column-resize true
     :show-filter true
     :selected-items ($- selection)
     :column-defs (map (fn [k] {:field k})
                       (:search-columns @config))}))
