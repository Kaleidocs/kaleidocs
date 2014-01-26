(defcontroller records-ctrl
  [$scope]
  ($->atom records records)
  (defn$ remove-record [id]
    (remove-entity! records id))
  (defn$ add-record []
    (let [record-id (gen-unique-id :records)
          default-keys (get @config :record-keys [])]
      (add-entity! records
                   nil
                   {:id record-id
                    :name (+ "New record #" record-id)
                    :fields
                    (map (fn [k] {:name k
                                  :value ""})
                         default-keys)}))))

(defcontroller record-ctrl
  [$scope]
  (defn$ filter-field [field]
    (true? (:deleted? field)))
  (defn$ reset []
    (def$ fields
      (-> records
          (find-entities {:id (:id ($- record))})
          first
          (get :fields))))
  (($- reset))
  (defn$ sync-record []
    (swap! records
           #(assoc-in % [(:id ($- record)) :fields] ($- fields))))
  (defn$ remove-field [index]
    (.splice ($- fields)
             index 1))
  (defn$ add-field []
    (def$ fields
      (conj ($- fields)
            {:name "" :value ""}))))
