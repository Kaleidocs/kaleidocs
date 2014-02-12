(def sockjs-url (+* window.location.protocol "//"
                    window.location.host
                    "/socket"))

(defsocket socket #(SockJS. sockjs-url nil
                            #_{:protocols_whitelist
                               ['xhr-polling]})
    {:debug true
     :on-open load-all})

(. socket on :templates
   (fn [msg-type data respond! _]
     (reset! templates data)))

(. socket on :config
   (fn [msg-type data respond! _]
     (reset! config data)))

(. socket on :profiles
   (fn [msg-type data respond! _]
     (reset! profiles data)))

(. socket on :records
   (fn [msg-type data respond! _]
     (reset! records data)))

(. socket on :status
   (fn [msg-type data respond! _]
     (reset! status data)))

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
  (. socket emit :records @records)
  (. socket emit :counter @id-counter)
  (. socket emit :config @config)
  (. socket emit :templates @templates)
  (reset! status "Data saved"))

(defn load-all []
  (. socket emit :init)
  (reset! status "Data loaded"))
