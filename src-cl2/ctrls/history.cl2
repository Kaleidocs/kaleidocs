(defcontroller history-ctrl
  [$scope]
  ($->atom records records)
  ($->atom history records
           (fn [x]
             (let [fields (map #(:fields %) (vals x))
                   fields->map
                   (fn [x]
                     (zipmap (map #(:name %) x)
                             (map #(:value %) x)))]
               (map fields->map fields))))
  (def$ grid-options
    {:data "history"
     :column-defs (map (fn [k] {:field k})
                       (:record-keys @config))}))
