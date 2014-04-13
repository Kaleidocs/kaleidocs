(ns kaleidocs.generate
  (:require [kaleidocs.merge :refer [merge-doc]]
            [taoensso.timbre :as timbre]
            [kaleidocs.models :refer :all]
            [kaleidocs.convert :refer :all]
            [n2w-vi.core :refer [number->words]]))

(def numberToTextVn
  (reify freemarker.template.TemplateMethodModelEx
    (exec [this args]
      (number->words (.getAsNumber (first (seq args)))))))

(defn long-date-vn [s]
  (apply format "ngày %s tháng %s năm %s"
         (clojure.string/split s #"/")))

(def longDateVn
  (reify freemarker.template.TemplateMethodModelEx
    (exec [this args]
      (long-date-vn (.toString (first (seq args)))))))

(def freemarker-utils
  {"numberToTextVn" numberToTextVn
   "longDateVn" longDateVn})

(defn unkeywordize [m]
    (zipmap (map name (keys m)) (vals m)))

(defn generate-records* [records]
  (doseq [current-record records
          :let [documents
                (clojure.string/split
                 (:documents current-record) #",")]]
    (doseq [current-document documents
            :let [current-output
                  (str (:id current-record) "_" current-document)]]
      (merge-doc (str templates-dir "/" current-document)
                 (str output-dir "/" current-output)
                 (merge (unkeywordize current-record)
                        freemarker-utils)))))

(defn generate-records [ids]
  (generate-records* (fetch-expanded-records ids)))

(defn generate-contract [id]
  (let [current-contract (fetch-contract id)
        record-ids (to-list (:records current-contract))
        records (fetch-expanded-records record-ids)]
    (generate-records* records)
    (doseq [current-document
            (map transform-fields (fetch-multidocs))
            :let [current-output
                  (str (:id current-contract) "_"
                       (:filename current-document))]]
      (merge-doc (str templates-dir "/" (:filename current-document))
                 (str output-dir "/" current-output)
                 ;; columns
                 (map #(str "TABLE." (name %))
                      (:fields current-document))
                 (merge (unkeywordize current-contract)
                        {"TABLE"
                         (mapv unkeywordize records)}
                        freemarker-utils)))))

#_
(defn gen-doc [single-templates multiple-templates table-keys
               records data-map]
  (doseq [t multiple-templates
          :let
          [extension
           (filename->extension t)
           generated-odf
           (str output-dir "/" (get data-map "PID") "_" (odf-filename t))]]
    (broadcast [:status (str "Merging " generated-odf)])
    (merge-doc (odf-template t)
               generated-odf
               (map #(str "TABLE." %) table-keys)
               (merge data-map {"TABLE" records}))
    (when (mso-file? t)
      (broadcast [:status (str "Exporting " generated-odf
                               " to " extension)])
      (convert-doc extension generated-odf output-dir)
      (.delete (clojure.java.io/file generated-odf)))
    (broadcast [:status (str "Finished " t)]))

  (doseq [t single-templates
          r records
          :let
          [extension
           (filename->extension t)
           generated-odf
           (str output-dir "/" (get r "id") "_" (odf-filename t))]]
    (broadcast [:status (str "Merging " t)])
    (merge-doc (odf-template t)
               generated-odf
               (merge data-map r))
    (when (mso-file? t)
      (broadcast [:status (str "Exporting " generated-odf
                               " to " extension)])
      (convert-doc extension generated-odf output-dir)
      (.delete (clojure.java.io/file generated-odf)))
    (broadcast [:status (str "Finished " t)]))

  (broadcast [:status (format "Producing documents #%s finished!"
                              (get data-map "PID"))]))
