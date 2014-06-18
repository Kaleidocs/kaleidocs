(ns insert
  (:require [kaleidocs.models :refer :all]
            [korma.core :refer :all]
            [korma.db :refer :all]
            [korma.sql.fns :refer :all]
            [cheshire.core :refer [generate-string parse-string]]))

;; sample data
(def sample-doc [{:filename "unc bidv tg.ods"
                  :fields ""}
                 {:filename "unc bidv vay.ods"
                  :fields ""}
                 {:filename "unc mb.ods"
                  :fields ""}])
(defn insert-doc []
  (insert
   document
   (values (conj sample-doc
                 {:filename "hdtd bidv.odt"
                  :fields "ID,remarks,id,money,company,account,bank"}))))

(defn insert-docgroup []
  (insert
   docgroup
   (values (into
            ;; 1 groups with many docs
            [{:name "UNC BIDV"
              :documents "unc bidv tg.ods,unc bidv vay.ods"}]
            ;; 3 groups with one doc
            (mapv (fn [x] {:documents (:filename x)
                           :name (:filename x)})
                  sample-doc)))))

;; 4 profiles
(defn insert-profile []
  (insert
   profile
   (values [{:company "CN Công ty CPĐT và XDCN - XNXD số 1",
             :account "2111 000 000 1921",
             :bank "BIDV Hà Nội",
             :city "Hà Nội"}
            {:company "CN Công ty CPĐT và XDCN - XNXD số 7",
             :account "2111 000 000 2155",
             :bank "BIDV Hà Nội",
             :city "Hà Nội"}
            {:company "CN Công ty CPĐT và XDCN - XNXD số 4",
             :account "2111 000 000 1994",
             :bank "BIDV Hà Nội",
             :city "Hà Nội"}
            {:company "CN Công ty CPĐT và XDCN - XNXD số 4",
             :account "051 110 173 2007",
             :bank "MB - CN Điện Biên Phủ",
             :city "Hà Nội"}
            {:company "Công ty CPĐT và Xây dựng 24 - ICIC",
             :account "2111 000 000 1976",
             :bank "BIDV Hà Nội",
             :city "Hà Nội"}
            {:company "Chi nhánh Công ty CPĐT và Xây dựng Công nghiệp",
             :account "310 10 000 871 083",
             :bank "BIDV - CN TP.HCM",
             :city "TP. Hồ Chí Minh"}
            {:company "Xí nghiệp Xây dựng số 9",
             :account "3101 000 000 6937",
             :bank "BIDV - CN TP.HCM",
             :city "TP. Hồ Chí Minh"}
            {:company "Công ty Điện lực Thanh Xuân",
             :account "10201 00000 54238",
             :bank "Chi nhánh Ngân hàng Công thương Thanh Xuân",
             :city "Hà Nội"}
            {:company "CN Công ty CPĐT và XDCN - XNXD số 5",
             :account "2111 000 000 1912",
             :bank "BIDV Hà Nội",
             :city "Hà Nội"}
            {:company "Công ty CPĐT và Xây dựng số 3 - ICIC",
             :account "2111 000 000 6768",
             :bank "BIDV Hà Nội",
             :city "Hà Nội"}
            {:company "Công ty CPĐT và Cơ giới xây dựng - ICIC",
             :account "2111 0000 253 032",
             :bank "BIDV Hà Nội",
             :city "Hà Nội"}
            {:company "BHXH Quận Thanh Xuân",
             :account "1505 202 901095",
             :bank "CN Ngân hàng NN và PTNT Quận Thanh Xuân",
             :city "Hà Nội"}
            {:company "Công ty cổ phần DIC Đồng Tiến",
             :account "6711 0000 105 289",
             :bank "BIDV Đồng Nai",
             :city "Đồng Nai"}
            {:company "Công ty TNHH XD và TM Kiên Thành",
             :account "2607 201 003 899",
             :bank "NN & PTNT huyện Thuận Thành - Bắc Ninh",
             :city "Bắc Ninh"}])))

(def sample-remarks
  ["ăn uống" "nghỉ ngơi" "vui chơi" "có thưởng"])

(defn create-sample-record []
  "random select from 4 profiles, 4 remarks and 4 docgroups"
  {:date (str (inc (rand-int 30)) "/04/2014")
   :money (* 1000200 (inc (rand-int 100)))
   :remarks (rand-nth sample-remarks)
   ;; :contract_id nil
   :docgroup_id (inc (rand-int 4))
   :profile_id (inc (rand-int 4))})

(defn create-sample-contract []
  (let [records (for [i (range 1 (+ 2 (rand-int 6)))] (inc (rand-int 1000)))]
    {:date (str (inc (rand-int 30)) "/04/2015")
     :records (clojure.string/join "," records)
     :sum (:sum (query-records-by-ids records))}))

(defn insert-record []
  (insert
   record
   (values (for [i (range 20)] (create-sample-record)))))

(defn insert-mass-record []
  (dotimes [n 50]
    (insert-record)))

(defn insert-contract []
  (insert
   contract
   (values (for [i (range 20)] (create-sample-contract)))))

(defn insert-fields []
  (insert
   custom_fields
   (values [{:entity "profile", :field "company"}
            {:entity "profile", :field "bank"}
            {:entity "profile", :field "account"}
            {:entity "profile", :field "city"}
            ;; Don't add money field as its type is bigint
            ;; {:entity "record", :field "money"}
            {:entity "record", :field "remarks"}
            {:entity "record", :field "date"}

            ;; ensure the initial fields are also in lobos.migrations
            ;;{:entity "record", :field "partner"}
            ;;{:entity "record", :field "project"}
            ])))

(defn insert-all []
  (insert-doc)
  (insert-docgroup)
  (insert-profile)
  (insert-fields)
  )

(defn insert-bunk []
  (println "Please wait, it may take several minutes to generate bunk data...")
  (insert-doc)
  (insert-docgroup)
  (insert-profile)
  (insert-mass-record)
  (insert-fields)
  (insert-contract))

(def entities (map name '[document docgroup profile record contract]))

(defn save-data [filename]
  (->> (for [entity entities]
         [entity (select (name->entity entity))])
       (into {})
       pr-str
       (spit filename)))

(defn load-data [filename]
  (let [m (read-string (slurp filename))]
    (doseq [entity entities]
      (insert (name->entity entity)
              (values (get m entity))))))
