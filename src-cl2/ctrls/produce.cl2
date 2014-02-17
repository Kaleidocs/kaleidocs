(defcontroller produce-ctrl
  [$scope date-filter]
  ($->atom produce produce)

  ($->atom multiple-templates templates
           (fn [x]
             (filter #(= "multiple" (:type %))
                     (vals x))))

  ($->atom single-templates templates
           (fn [x]
             (filter #(= "single" (:type %))
                     (vals x))))
  (let [[d m y]
        (-> (Date.)
            .getTime
            (date-filter
             "dd/MM/yyyy")
            (.split "/"))]
    (def$ DD d)
    (def$ MM m)
    (def$ YYYY y))
  ($->atom PID id-counter
           (fn [c]
             (inc (get c :pid 0))))

  (defn$ get-records [ids]
    (find-entities records #(contains? (set ids) (:id %))))

  (defn get-date-fields []
    {:DD ($- DD)
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
    (let [multiple-templates (filter-multiple-templates)
          data [(filter-single-templates)
                multiple-templates
                (:table-keys @config)
                (export-records (:records @produce))
                (merge (:table @produce)
                       (when (< 0 (count multiple-templates))
                         (let [pid (inc (gen-unique-id :pid))]
                           (assoc-contract! pid (:record-ids @produce))
                           {:PID pid}))
                       (get-date-fields))]]
      (. socket emit :gen-doc data))))
