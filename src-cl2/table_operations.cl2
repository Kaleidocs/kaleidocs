(defn add-row! [table]
  (let [new-row-id (inc (:row-count table))]
    (set! (:row-count table) new-row-id)
    (set! (-> table :rows (get new-row-id)) {:id new-row-id
                                             :values {}})))

(defn remove-row! [table row-num]
  (delete (-> table :rows (get row-num))))

(defn add-column! [table col-name]
  (let [new-col-id (inc (:column-count table))]
    (set! (:column-count table) new-col-id)
    (set! (-> table :columns (get new-col-id))
          {:id new-col-id :name col-name})))

(defn remove-column! [table col-num]
  (delete (-> table :columns (get col-num)))
  (doseq [[_ row] (:rows table)]
    (delete (-> (:values row) (get col-num)))))
