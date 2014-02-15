(defcontroller profiles-ctrl
  [$scope]
  ($->atom profiles profiles vals)
  (def$ order-key "name")
  (defn$ remove-profile [id]
    (remove-entity! profiles id))
  (defn$ add-profile []
    (let [profile-id (gen-unique-id :profiles)
          default-keys (get @config :profile-keys [])]
      (add-entity! profiles
                   nil
                   {:id profile-id
                    :name (+ "New profile #" profile-id)
                    :fields
                    (map (fn [k] {:name k
                                  :value ""})
                         default-keys)})
      (reset! status (+ "New profile #" profile-id
                        " added")))))

(defcontroller profile-ctrl
  [$scope]
  (defn$ filter-field [field]
    (true? (:deleted? field)))
  (defn$ reset []
    (def$ fields
      (-> profiles
          (find-entities {:id (:id ($- profile))})
          first
          (get :fields))))
  (($- reset))
  (defn$ sync-profile []
    (swap! profiles
           #(assoc-in % [(:id ($- profile)) :fields] ($- fields))))
  (defn$ remove-field [index]
    (.splice ($- fields)
             index 1))
  (defn$ add-field []
    (def$ fields
      (conj ($- fields)
            {:name "" :value ""}))))
