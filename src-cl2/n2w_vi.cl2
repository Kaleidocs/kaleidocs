(defn str [& xs]
  (.join xs ""))

(def digit->word-map
  (zipmap (map #(first (str %)) (range 10))
          (map str '(không một hai ba bốn năm sáu bảy tám chín))))

(defn digit->word [z]
  (get digit->word-map z))

(defn group-of-two->word [y z]
  (if (= \1 y)
    (let [tail (if-not (= \0 z)
                 (str " " (digit->word z)))]
      (str "mười"
           tail))

    (str (digit->word y) " mươi"
         (when (not= \0 z)
           (str " "
                (cond
                 (and (= \1 z) (not (contains? #{\0 \1} y)))
                 "mốt"
                 (and (= \4 z) (not (contains? #{\0 \1} y)))
                 "tư"
                 (and (= \5 z) (not= \0 y))
                 "lăm"
                 :default
                 (digit->word z)))))))

(defn group-of-three->word [x y z]
  (str
   (digit->word x)
   (if-not (= \0 x y z) " trăm")
   (cond
    (= \0 y z)
    ""

    (= \0 y)
    (str " linh " (digit->word z))

    :default
    (str " " (group-of-two->word y z)))))

(defn number->word [n]
  (number->word-helper (seq (str n))))

(defn link-groups [digits level paster]
  (let [n-of-digits (count digits)
        remainder (rem n-of-digits level)
        digits-to-process (if (zero? remainder)
                            level
                            remainder)
        repeated (dec (/ (- n-of-digits digits-to-process)
                         level))
        head (take digits-to-process digits)
        tail (drop digits-to-process digits)]
    (str (number->word-helper head) " "
         paster
         (if (apply = (cons \0 tail))
           (when (< 0 repeated)
             (apply str " " (interpose " " (repeat repeated paster))))
           (str ", " (number->word-helper tail))))))

(defn number->word-helper [digits]
  (let [n-of-digits (count digits)]
    (cond
     (< 9 n-of-digits)
     (link-groups digits 9 "tỷ")

     (< 6 n-of-digits)
     (link-groups digits 6 "triệu")

     (< 3 n-of-digits)
     (link-groups digits 3 "nghìn")

     (= 3 n-of-digits)
     (apply group-of-three->word digits)

     (= 2 n-of-digits)
     (apply group-of-two->word digits)

     (= 1 n-of-digits)
     (apply digit->word digits))))
