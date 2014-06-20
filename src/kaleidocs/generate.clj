(ns kaleidocs.generate
  (:require [kaleidocs.merge :refer [merge-doc]]
            [kaleidocs.utils :refer [escape-ampersands long-date-vn unkeywordize]]
            [taoensso.timbre :as timbre]
            [kaleidocs.models :refer :all]
            [kaleidocs.convert :refer :all]
            [n2w-vi.core :refer [number->words]]))

(def numberToTextVn
  (reify freemarker.template.TemplateMethodModelEx
    (exec [this args]
      (number->words (.getAsNumber (first (seq args)))))))

(def longDateVn
  (reify freemarker.template.TemplateMethodModelEx
    (exec [this args]
      (long-date-vn (.toString (first (seq args)))))))

(def freemarker-utils
  {"numberToTextVn" numberToTextVn
   "longDateVn" longDateVn})

(defn generate-record* [record]
  (let [documents (-> record :documents
                      (clojure.string/split #","))]
    (for [document documents
          :let [output-file (str (:id record) "_" document)]]
      (do (merge-doc (str templates-dir "/" document)
                     (str output-dir "/" output-file)
                     (merge (unkeywordize record) freemarker-utils))
          output-file))))

(defn generate-records [ids]
  (mapcat #(-> % escape-ampersands generate-record*)
          (fetch-records ids)))

(defn generate-contract [id]
  (let [current-contract (escape-ampersands (fetch-contract id))
        record-ids (split-string->list (:records current-contract))
        records (map-indexed
                 #(assoc (escape-ampersands %2) "ID" (inc %1))
                 (fetch-records record-ids))]
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
