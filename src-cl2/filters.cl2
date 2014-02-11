(deffilter filter-deleted []
  [items]
  (filter #(not (= :deleted %)) items))

(deffilter filter-amount-fields []
  [fields suffix]
  (filter
   #(end-with? (:name %) suffix)
   fields))

(deffilter filter-amount-table-fields []
  [columns suffix]
  (for [[_ v] columns
        :when (end-with? (:name v) suffix)]
    v))

(deffilter filter-by-profile []
  [records profile-id]
  (for [[_ v] records
        :when (=== profile-id (:profile v))
        #_(end-with? (:name v) suffix)]
    v))

(defn fields->map
  "Converts vector of fields to a single map.
  Works on profiles and records"
  [x]
  (zipmap (map #(:name %) x)
          (map #(:value %) x)))

(deffilter fields->map []
  [x]
  (fields->map x))

(deffilter sum-by-column []
  [table col-name]
  (let [col-id nil]
    (if (not (= {} (:rows table)))
      (doseq [[k v] (:columns table)]
        (when (= col-name (:name v))
          (set! col-id k))))
    (when (not (nil? col-id))
      (let [rows (for [[_ row] (:rows table)
                       [k v] (:values row)
                       :when (= col-id k)]
                   (parseInt v))]
        (apply + (filter #(not (=== NaN %)) rows))))))

(deffilter sum-of-column []
  [records col-name]
  (->> records
        (map #(:fields %))
        (map #(fields->map %))
        (map #(get % col-name))
        (map #(parseInt %))
        (remove isNaN)
        (remove nil?)
        (apply +)))

(defn amount-field? [field-name]
  (end-with? field-name (:amount-suffix @config)))

(deffilter amount-field? []
  [field-name]
  (amount-field? field-name))

(defn amount-in-words
  [amount]
  (load-file "n2w_vi.cl2")
  (number->words* (seq (if (number? amount)
                              (+ "" amount)
                              amount))
                  ","))

(deffilter amount-in-words []
  [amount]
  (amount-in-words amount))

(deffilter export-table []
  [k1 k2 v1 v2]
  (zipmap [k1 k2] [v1 v2]))

(deffilter pr-str []
  [x]
  (pr-str x))
