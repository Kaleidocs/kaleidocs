(ns kaleidocs.generate
  (:require [kaleidocs.merge :refer [merge-doc]]
            [taoensso.timbre :as timbre]
            [kaleidocs.models :refer :all]
            [kaleidocs.convert :refer :all]))

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