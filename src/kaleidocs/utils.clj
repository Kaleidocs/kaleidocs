(ns kaleidocs.utils
  (:require [vietnamese-utils.core :refer [normalize-diacritics]]
            [clojure.string :as string]
            [ring.util.response :refer [file-response]]
            [ring.util.codec :refer [url-decode]]))

(defn file-response-as [path filename]
  (assoc-in (file-response path)
            [:headers "Content-Disposition"]
            (format "attachment;filename=%s" filename)))

(defn escape-ampersands* [s]
  (if (string? s)
    (string/escape s {\& \＆})
    s))

(defn escape-ampersands [m]
  (reduce (fn [m0 [k v]]
            (assoc m0 k (escape-ampersands* v)))
          nil m))

(defn normalize-diacritics-map [m]
  (reduce (fn [m0 [k v]]
            (assoc m0 k (if (string? v)
                          (normalize-diacritics v true)
                          v)))
          nil m))

(defn long-date-vn [s]
  (apply format "ngày %s tháng %s năm %s"
         (clojure.string/split s #"/")))

(defn unkeywordize [m]
  (reduce (fn [m0 [k v]] (assoc m0 (name k) v))
          nil m))

(defn find-order-key
  [s]
  (second (re-find #"sorting\[([a-zA-Z_]+)\]" s)))

(defn find-order-kv
  [params]
  (let [order-key (some find-order-key (keys params))
        order-value (if order-key
                      (case (get params (format "sorting[%s]" order-key))
                        "asc" :ASC
                        "desc" :DESC
                        nil)
                      :DESC)]
    [(or (keyword order-key) :id) order-value]))

(defn find-filter-key
  [s]
  (second (re-find #"filter\[([a-zA-Z_]+)\]" s)))

(defn find-filter-kvs
  [params]
  (let [filter-keys (remove nil? (map find-filter-key (keys params)))
        normalized-value-for
        (fn [k] (-> (get params (format "filter[%s]" k))
                    url-decode
                    (normalize-diacritics true)))]
    (map (fn [k]
           [(keyword k)
            (normalized-value-for k)])
         filter-keys)))
