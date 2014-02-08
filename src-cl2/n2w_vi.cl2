(def digit->words-map
  (zipmap (map #(first (str %)) (range 10))
          (map str '(không một hai ba bốn năm sáu bảy tám chín))))

(defn digit->words [z]
  (get digit->words-map z))

(defn group-of-two->words [y z]
  (if (= \1 y)
    (let [tail (if-not (= \0 z)
                 (str " " (digit->words z)))]
      (str "mười"
           tail))

    (str (digit->words y) " mươi"
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
                 (digit->words z)))))))

(defn group-of-three->words [x y z]
  (str
   (digit->words x)
   (if-not (= \0 x y z) " trăm")
   (cond
    (= \0 y z)
    ""

    (= \0 y)
    (str " linh " (digit->words z))

    :default
    (str " " (group-of-two->words y z)))))

(defn number->words
  ([n]
     (number->words* (seq (str n)) nil))
  ([n separator]
     (number->words* (seq (str n)) separator)))

(defn link-groups [digits level paster separator]
  (let [n-of-digits (count digits)
        remainder (rem n-of-digits level)
        digits-to-process (if (zero? remainder)
                            level
                            remainder)
        repeated (dec (/ (- n-of-digits digits-to-process)
                         level))
        head (take digits-to-process digits)
        tail (drop digits-to-process digits)]
    (str (number->words* head) " "
         paster
         (if (apply = (cons \0 tail))
           (when (< 0 repeated)
             (apply str " " (interpose " " (repeat repeated paster))))
           (str separator " " (number->words* tail))))))

(defn number->words*
  ([digits]
     (number->words* digits nil))
  ([digits separator]
     (let [n-of-digits (count digits)]
       (cond
        (< 9 n-of-digits)
        (link-groups digits 9 "tỷ" separator)

        (< 6 n-of-digits)
        (link-groups digits 6 "triệu" separator)

        (< 3 n-of-digits)
        (link-groups digits 3 "nghìn" separator)

        (= 3 n-of-digits)
        (apply group-of-three->words digits)

        (= 2 n-of-digits)
        (apply group-of-two->words digits)

        (= 1 n-of-digits)
        (apply digit->words digits)))))
