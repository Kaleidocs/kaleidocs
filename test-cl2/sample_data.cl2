(def profiles
  (atom {}))

(def produce
  (atom []))

(def id-counter
  (atom {}))

(defn set-counter!
  "Set counter number for a given key."
  [k n]
  (swap! id-counter #(assoc % (serialize k) n)))

(defn gen-unique-id
  "Generates unique id for a given key.
  Keys can be integers, strings or arrays of those
  as they are serialized/deserialized via JSON."
  [k]
  (if (contains? @id-counter (serialize k))
    (swap! id-counter
           #(update-in % [(serialize k)] inc))
    (set-counter! k 0))
  (get @id-counter (serialize k)))

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

(def tables (atom {}))
