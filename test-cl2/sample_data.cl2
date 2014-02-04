(def profiles
  (atom {}))

(def records
  (atom {}))

(def produce
  (atom {:recordIds []}))

(def id-counter
  (atom {}))

(def templates
  (atom {}))

(def config
  (atom {:profile-keys [],
         :search-columns [],
         :record-keys [],
         :produce-keys [],
         :amount-suffix "",
         :amount-iw-suffix "",
         :table-keys [],
         :sum-column ""}))

(def status
  (atom nil))

(defn set-counter!
  "Set counter number for a given key."
  [k n]
  (swap! id-counter #(assoc % k n)))

(defn gen-unique-id
  "Generates unique id for a given key.
  Keys can be integers, strings or arrays of those
  as they are serialized/deserialized via JSON."
  [k]
  (if (contains? @id-counter k)
    (swap! id-counter
           #(update-in % [k] inc))
    (set-counter! k 0))
  (get @id-counter k))

(def tables (atom {}))
