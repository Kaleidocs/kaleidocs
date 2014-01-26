(defcontroller produce-ctrl
  [$scope]
  (defn$ filter-field [field]
    (true? (:deleted? field)))
  (defn$ reset []
    (def$ fields @produce))
  (if (= [] @produce)
    (let [default-keys (get @config :produce-keys [])]
      (reset! produce (map (fn [field]
                             {:name field
                              :value ""}) default-keys))))
  (($- reset))
  (defn$ sync-produce []
    (reset! produce ($- fields)))
  (defn$ remove-field [index]
    (.splice ($- fields)
             index 1))
  (defn$ add-field []
    (def$ fields
      (conj ($- fields)
            {:name "" :value ""}))))
