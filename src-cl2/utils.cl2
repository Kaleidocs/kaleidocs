(defn end-with? [x s]
  (== s (.match x (+ s "$"))))

(defn format-number [coll]
  ;; TODO: bug in core-cl2's partition
  ;;(.join (remove nil? (partition 3 x)) \.)
  ;; TODO: bug in (drop 0 coll) -> nil
  (when-not (empty? coll)
    (let [r (rem (count coll) 3)]
      (if (zero? r)
        (.join (map #(.join % "") (partition 3 coll)) \.)
        (let [tail (partition 3 (drop r coll))
              head (take r coll)]
          (.join (map #(.join % "") (cons head tail)) \.))))))

(defn format-amount&add-iw! [obj]
  (doseq [[k v] obj]
    (when (amount-field? k)
      (set! (get obj k) (format-number (seq v)))
      (set! (get obj
                 (+ k (:amount-iw-suffix @config)))
            (amount-in-words v))))
  obj)

(defn index->id! [coll]
  (doseq [[k v] coll]
    (set! (get v "ID") (inc (parseInt k))))
  coll)

(defn get-profile-as-map [id]
  (fields->map (:fields (first (find-entities profiles {:id id})))))

(defn export-records [records]
  (index->id!
   (map #(merge (format-amount&add-iw! (fields->map (:fields %)))
                {:id (:id %)}
                (get-profile-as-map (:profile %)))
        records)))

(defn get-single-templates []
  (map #(:filename %) (find-entities templates {:type "single"})))

(defn get-multiple-templates []
  (map #(:filename %) (find-entities templates {:type "multiple"})))

(defn assoc-contract* [pid record]
  (->> pid
       (conj (:contract record))
       set
       seq
       (assoc record :contract)))

(defn assoc-contract! [pid record-ids]
  (find-&-update-entities!
   records
   #(contains? (set record-ids) (:id %))
   #(assoc-contract* pid %)))

(defn columns->empty-map [columns]
  (let [m {}]
    (doseq [c columns]
      (set! (get m c) ""))
    m))
