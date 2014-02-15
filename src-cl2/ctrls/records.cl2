(defcontroller records-ctrl
  [$scope date-filter]
  ($->atom records records)
  ($->atom profiles profiles vals)
  (def$ profile-filter "")
  (defn$ remove-record [id]
    (remove-entity! records id))
  (defn today! []
    (-> (Date.)
        .getTime
        (date-filter
         "dd/MM/yyyy")))
  (defn$ add-record [profile-id]
    (let [record-id (gen-unique-id :records)
          default-keys (get @config :record-keys [])]
      (add-entity! records
                   nil
                   {:id record-id
                    :profile profile-id
                    :fields
                    (map (fn [k] {:name k
                                  :value (if (= "date" k)
                                           (today!)
                                           "")})
                         default-keys)})
      (reset! status (+ "New record #" record-id
                        " added")))))

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
