(defcontroller history-ctrl
  [$scope]
  ($->atom records records)
  ($->atom history records
           (fn [x]
             (let [ids-&-fields (map (fn [y]
                                       [(:id y) (:fields y)])
                                     (vals x))]
               (map #(merge (fields->map (second %))
                            {:id (first %)})
                    ids-&-fields))))
  (def$ selection [])
  (def$ grid-options
    {:data "history"
     :multi-select false
     :show-filter true
     :selected-items ($- selection)
     :column-defs (map (fn [k] {:field k})
                       (:search-columns @config))}))
