(defcontroller produce-ctrl
  [$scope]
  ($->atom produce produce)
  (let [d (Date.)]
    (def$ DD (.getDate d))
    (def$ MM (inc (.getMonth d)))
    (def$ YYYY (.getFullYear d)))
  ($->atom PID id-counter
           (fn [c]
             (inc (get c :pid 0))))
  (defn$ gen-doc [& data]
    (. socket emit :gen-doc data))
  (defn$ get-records [ids]
    (find-entities records #(contains? (set ids) (:id %))))
  (defn$ get-profile [records]
    (first (find-entities profiles {:id (:profile (first records))})))
  (defn get-auto-fields []
    {:PID (inc (gen-unique-id :pid))
     :DD ($- DD)
     :MM ($- MM)
     :YYYY ($- YYYY)})
  )
