(defcontroller tables-ctrl
  [$scope]
  ($->atom tables tables)
  (defn$ remove-table [id]
    (remove-entity! tables id))
  (defn$ add-table []
    (let [table-id (gen-unique-id :tables)
          default-keys (get @config :table-keys [])]
      (add-entity! tables
                   nil
                   (let [column-count (count default-keys)
                         ids (range column-count)]
                     {:id table-id
                      :name (+ "new_table_" table-id)
                      :column-count column-count
                      :row-count 0
                      :columns (zipmap ids
                                      (for [[id column] default-keys]
                                        {:id id :name column}))
                      :rows {}})))))

(defcontroller table-ctrl
  [$scope]
  (defn$ sync-table []
    (swap! tables
           #(assoc % (:id ($- table))
                   ($- table))))
  (defn$ load-table []
    (def$ table (get @tables (:id ($- table)))))

  (def$ addRow #(add-row! ($- table)))
  (def$ removeRow #(remove-row! ($- table) %))
  (def$ addColumn #(add-column! ($- table) %))
  (def$ removeColumn #(remove-column! ($- table) %)))
