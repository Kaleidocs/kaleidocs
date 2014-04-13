(ns kaleidocs.convert
  (:require [clj-commons-exec :as exec]))

(def templates-dir "templates")
(def output-dir "output")

(defn mso-file? [s]
  (or (.endsWith s ".doc")
      (.endsWith s ".docx")
      (.endsWith s ".xls")
      (.endsWith s ".xlsx")))

(defn multi-doc? [document-record]
  (and (.endsWith (:filename document-record) ".odt")
       (when-let [fields (:fields document-record)]
         (< 0 (count (clojure.string/split fields #","))))))

(defn odf-file? [s]
  (or (.endsWith s ".odt")
      (.endsWith s ".ods")))

(defn filename->target-format [s]
  (cond
   (or (.endsWith s ".doc")
       (.endsWith s ".docx"))
   "odt"
   (or (.endsWith s ".xls")
       (.endsWith s ".xlsx"))
   "ods"))

(defn filename->extension [s]
  (cond
   (.endsWith s ".doc")
   "doc"
   (.endsWith s ".docx")
   "docx"
   (.endsWith s ".xls")
   "xls"
   (.endsWith s ".xlsx")
   "xlsx"))

(defn odf-filename [s]
  (-> s
   (clojure.string/replace #".doc$" ".odt")
   (clojure.string/replace #".docx$" ".odt")
   (clojure.string/replace #".xls$" ".ods")
   (clojure.string/replace #".xlsx$" ".ods")))

(defn odf-template [s]
  (str templates-dir "/" (odf-filename s)))

(defn get-libreoffice-executable []
  (let [os (.toLowerCase (System/getProperty "os.name"))]
    (cond
     (.startsWith os "windows")
     "c:\\program files\\libreoffice 4\\program\\soffice.exe"

     :default
     "libreoffice")))

(defn convert-doc [target-format input out-dir]
  (let [exec-out
        (:out @(exec/sh [(get-libreoffice-executable)
                         "--headless"
                         "--convert-to"
                         target-format
                         (.getAbsolutePath
                          (clojure.java.io/file input))
                         "--outdir"
                         (.getAbsolutePath
                          (clojure.java.io/file out-dir))]))]
    (when (string? exec-out)
      (.startsWith exec-out "convert "))))
