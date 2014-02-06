(defcontroller history-ctrl
  [$scope]
  ($->atom records records)
  ($->atom history records
           (fn [x]
             (let [fields (map #(:fields %) (vals x))]
               (map fields->map fields))))
  (def$ selection [])
  (def$ grid-options
    {:data "history"
     :multi-select false
     :show-filter true
     :selected-items ($- selection)
     :column-defs (map (fn [k] {:field k})
                       (:search-columns @config))}))
