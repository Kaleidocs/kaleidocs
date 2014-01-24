(load-file "angular-cl2/src/core.cl2")
(load-file "socket-cl2/src/client.cl2")
(load-file "atom-crud/src/core.cl2")
(load-file "../test-cl2/sample_data.cl2")
(load-file "table_operations.cl2")

(def sockjs-url (+* window.location.protocol "//"
                    window.location.host
                    "/socket"))

(defsocket socket #(SockJS. sockjs-url nil
                            #_{:protocols_whitelist
                               ['xhr-polling]})
    {:debug true})

(. socket on :config
   (fn [msg-type data respond! _]
     (reset! config data)))

(. socket on :profiles
   (fn [msg-type data respond! _]
     (reset! profiles data)))

(. socket on :counter
   (fn [msg-type data respond! _]
     (reset! id-counter data)))

(defn save-all []
  (. socket emit :profiles @profiles)
  (. socket emit :counter @id-counter)
  (. socket emit :config @config))

(defapp my-app [ngRoute xeditable ngTagsInput angularFileUpload])

;; don't have to specify app name as compiler remember the last app name
;; defined in `defapp`
(defroute
  "/debug"
  {:controller 'debug-ctrl
   :template
   (hiccup
    [:h3 "Config"]
    [:div "{{config}}"]
    [:h3 "Profiles"]
    [:div "{{profiles}}"]
    [:h3 "Tables"]
    [:div "{{tables}}"]
    [:h3 "Produce"]
    [:div "{{produce}}"])}
  "/profile/:profileId"
  ['profile-ctrl "partials/profile.html"]

  "/templates"
  ['templates-ctrl "partials/templates.html"]

  "/profiles"
  ['profiles-ctrl "partials/profiles.html"]

  "/tables"
  ['tables-ctrl "partials/tables.html"]

  "/produce"
  ['produce-ctrl "partials/produce.html"]

  "/config"
  ['config-ctrl "partials/config.html"]

  :default "/debug")

(defdirective my-directive
  []
  ;; can be a directive-definition object or a link function
  (fn [scope elm attrs]
    (.
     scope
     ($watch
      (:my-directive attrs)
      (fn [value] (. elm (text (+ value 4))))))))

(defcontroller my-ctrl
  [$scope my-service]
  (def$ some-number 12)
  (defn$ add-two [n] {:result (+ n 2)})
  (defn$ service-add [n]
    (my-service.add-three n)))

(defcontroller profiles-ctrl
  [$scope]
  ($->atom profiles profiles)
  (defn$ remove-profile [id]
    (remove-entity! profiles id))
  (defn$ add-profile []
    (let [profile-id (gen-unique-id :profiles)
          default-keys (get @config :profile-keys [])]
      (add-entity! profiles
                   nil
                   {:id profile-id
                    :name (+ "New profile #" profile-id)
                    :fields
                    (map (fn [k] {:name k
                                  :value ""})
                         default-keys)}))))

(defcontroller profile-ctrl
  [$scope]
  (defn$ filter-field [field]
    (true? (:deleted? field)))
  (defn$ reset []
    (def$ fields
      (-> profiles
          (find-entities {:id (:id ($- profile))})
          first
          (get :fields))))
  (($- reset))
  (defn$ sync-profile []
    (swap! profiles
           #(assoc-in % [(:id ($- profile)) :fields] ($- fields))))
  (defn$ remove-field [index]
    (.splice ($- fields)
             index 1))
  (defn$ add-field []
    (def$ fields
      (conj ($- fields)
            {:name "" :value ""}))))

(defcontroller produce-ctrl
  [$scope]
  (defn$ filter-field [field]
    (true? (:deleted? field)))
  (defn$ reset []
    (def$ fields @produce))
  (if (= [] @produce)
    (let [default-keys (get @config :produce-keys [])]
      (reset! produce (map (fn [field]
                             {:name field
                              :value ""}) default-keys))))
  (($- reset))
  (defn$ sync-produce []
    (reset! produce ($- fields)))
  (defn$ remove-field [index]
    (.splice ($- fields)
             index 1))
  (defn$ add-field []
    (def$ fields
      (conj ($- fields)
            {:name "" :value ""}))))

(def config
  (atom {:profile-keys ["NAME" "ADDRESS"],
         :search-columns ["NAME" "ADDRESS"],
         :produce-keys ["CITY" "CONTRACT"],
         :amount-suffix "_AMOUNT",
         :amount-iw-suffix "_IW",
         :table-keys ["TASK" "MONEY_AMOUNT"],
         :sum-column "MONEY_AMOUNT"}))

(defcontroller config-ctrl
  [$scope]
  ($->atom config config))

(defcontroller debug-ctrl
  [$scope]
  ($->atom tables tables)
  ($->atom profiles profiles)
  ($->atom produce produce)
  ($->atom config config))

(defcontroller generated-ctrl
  [$scope]
  ($->atom config config)
  ($->atom tables tables)
  )

(defcontroller select-ctrl
  [$scope])

(defcontroller templates-ctrl
  [$scope $upload]
  (defn$ onFileSelect [files]
    (doseq [file files]
      (def$ upload
        (-> $upload
            (.upload
             {:url "upload"
              :method "POST"
              :data {:hello "world"}
              :file file})
            (.success (fn [data status]))
            (.error (fn [data status]
                      (alert (+ "Error" data status)))))))))

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

;; example of specifying app name
(defservice my-app my-service
 []
 (this->!)
 (defn! add-three [n] (+ n 3)))

;; generic defmodule usage
(defmodule my-app
  (:filter (my-filter [] [s] (+ s 5))))

(deffilter your-filter []
  [s]
  (+ s 6))

(deffilter filter-deleted []
  [items]
  (filter #(not (= :deleted %)) items))

(defn end-with? [x s]
  (== s (.match x (+ s "$"))))

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

(deffilter amount-in-words []
  [amount]
  (load-file "n2w_vi.cl2")
  (number->word-helper (seq (if (number? amount)
                              (+ "" amount)
                              amount))))

(.run my-app
      (fn-di [editableOptions]
        (set! (:theme editableOptions) "bs3")))
