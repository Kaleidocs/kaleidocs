(def profiles
  (atom {}))

(def id-counter
  (atom {}))

(defn gen-unique-id
  "Generates unique id for a given key."
  [k]
  (if (contains? @id-counter k)
    (swap! id-counter
           #(update-in % [k] inc))
    (swap! id-counter
           #(assoc % k 0)))
  (get @id-counter k))

(let [profile-id (gen-unique-id :profiles)]
  (add-entity! profiles
               nil
               {:id profile-id
                :name "Company A"
                :fields [{:name "FOO"
                          :value "Some text"}
                         {:name "BAR"
                          :value "Some text"}]}))

(let [profile-id (gen-unique-id :profiles)]
  (add-entity! profiles
               nil
               {:id profile-id
                :name "Company B"
                :fields [{:name "FOO"
                          :value "Some texty"}
                         {:name "BAR"
                          :value "Some text"}]}))
