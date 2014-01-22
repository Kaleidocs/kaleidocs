(def profiles
  (atom {}))

(def produce
  (atom []))

(def id-counter
  (atom {}))

(defn set-counter!
  "Set counter number for a given key."
  [k n]
  (swap! id-counter #(assoc % k n)))

(defn gen-unique-id
  "Generates unique id for a given key."
  [k]
  (if (contains? @id-counter k)
    (swap! id-counter
           #(update-in % [k] inc))
    (set-counter! k 0))
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

(def tables (atom {}))

(let [table-id (gen-unique-id :tables)]
  (add-entity! tables
               nil
               {:id table-id
                :name "Lalo"
                :columns [:mname :city]
                :fields [{:mname "Mu" :city "x"}
                         {:mname "Nar" :city "yy"}]}))

(let [table-id (gen-unique-id :tables)]
  (add-entity! tables
               nil
               {:id table-id
                :name "Tatoot"
                :columns [:cname :address :talk]
                :fields [{:cname "Fu" :address "f"}
                         {:cname "Ku" :address "k"}
                         {:cname "Du" :address "d"}
                         {:cname "Nu" :address "n"}
                         {:cname "Ba" :address "b"}]}))
