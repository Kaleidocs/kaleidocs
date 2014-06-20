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
  (let [contract            (escape-ampersands (fetch-contract id))
        record-ids          (split-string->list (:records contract))
        indexed-records     (map-indexed
                             #(assoc (escape-ampersands %2) "ID" (inc %1))
                             (fetch-records record-ids))
        output-record-files (mapcat generate-record* indexed-records)]
    (->
     (for [contract-document (map transform-fields (fetch-multidocs))
           :let [contract-output
                 (str (:id contract) "_" (:filename contract-document))]]
       (do (merge-doc (str templates-dir "/" (:filename contract-document))
                      (str output-dir "/" contract-output)
                      ;; columns
                      (map #(str "TABLE." (name %)) (:fields contract-document))
                      (merge (unkeywordize contract)
                             {"TABLE" (mapv unkeywordize indexed-records)}
                             freemarker-utils))
           contract-output))
     (concat output-record-files))))
