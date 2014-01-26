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

(. socket on :templates
   (fn [msg-type data respond! _]
     (reset! templates data)))

(. socket on :config
   (fn [msg-type data respond! _]
     (reset! config data)))

(. socket on :profiles
   (fn [msg-type data respond! _]
     (reset! profiles data)))

(. socket on :counter
   (fn [msg-type data respond! _]
     (reset! id-counter data)))

(. socket on :new-template
   (fn [msg-type data respond! _]
     (let [template-id (gen-unique-id :templates)]
       (add-entity! templates
                    nil
                    {:id template-id
                     :filename (:filename data)}))))

(defn save-all []
  (. socket emit :profiles @profiles)
  (. socket emit :counter @id-counter)
  (. socket emit :config @config)
  (. socket emit :templates @templates))

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
    [:h3 "Templates"]
    [:div "{{templates}}"]
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

(load-file "ctrls/profiles.cl2")
(load-file "ctrls/tables.cl2")
(load-file "ctrls/produce.cl2")

(defcontroller config-ctrl
  [$scope]
  ($->atom config config))

(defcontroller debug-ctrl
  [$scope]
  ($->atom tables tables)
  ($->atom profiles profiles)
  ($->atom templates templates)
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
  ($->atom templates templates)
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
