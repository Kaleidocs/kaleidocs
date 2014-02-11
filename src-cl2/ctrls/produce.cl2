(defcontroller produce-ctrl
  [$scope]
  ($->atom produce produce)

  ($->atom multiple-templates templates
           (fn [x]
             (filter #(= "multiple" (:type %))
                     (vals x))))

  ($->atom single-templates templates
           (fn [x]
             (filter #(= "single" (:type %))
                     (vals x))))
  (let [d (Date.)]
    (def$ DD (.getDate d))
    (def$ MM (inc (.getMonth d)))
    (def$ YYYY (.getFullYear d)))
  ($->atom PID id-counter
           (fn [c]
             (inc (get c :pid 0))))

  (defn$ get-records [ids]
    (find-entities records #(contains? (set ids) (:id %))))

  (defn$ get-profile [records]
    (first (find-entities profiles {:id (:profile (first records))})))

  (defn get-auto-fields []
    {:PID (inc (gen-unique-id :pid))
     :DD ($- DD)
     :MM ($- MM)
     :YYYY ($- YYYY)})

  (defn filter-single-templates []
    (->> ($- single-templates)
         (filter #(true? (:checked %)))
         (map #(:filename %))))

  (defn filter-multiple-templates []
    (->> ($- multiple-templates)
         (filter #(true? (:checked %)))
         (map #(:filename %))))

  (defn$ gen-doc []
    (let [data [(filter-single-templates)
                (filter-multiple-templates)
                (:table-keys @config)
                (export-records (:records @produce))
                (merge (:profile @produce)
                       (:table @produce)
                       (get-auto-fields))]]
      (. socket emit :gen-doc data))))
