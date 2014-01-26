(defcontroller templates-ctrl
  [$scope $upload]
  ($->atom templates templates)
  (defn$ remove-template [id filename]
    (. socket emit [:delete-file filename])
    (remove-entity! templates id))
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
