(load-file "core.cl2")

(defn make-sample-table []
  {:id 0
   :name "tabtab"
   :column-count 1
   :row-count 1
   :columns {0 {:id 0 :name "foo"}
             1 {:id 1 :name "bar"}}
   :rows {0 {:id 0
             :values {0 "some text"       ;; col `foo`
                      1 "an other text"}} ;; col `bar`
          1 {:id 1
             :values {0 "cell"
                      1 "value"}}}})

(deftest table-rows-operations
  (let [sample-table (make-sample-table)]
    (add-row! sample-table)
    (is (= sample-table
           {:id 0
            :name "tabtab"
            :column-count 1
            :row-count 2
            :columns {0 {:id 0 :name "foo"}
                      1 {:id 1 :name "bar"}}
            :rows {0 {:id 0
                      :values {0 "some text"
                               1 "an other text"}}
                   1 {:id 1
                      :values {0 "cell"
                              1 "value"}}
                   2 {:id 2
                      :values {}}}}))
    (remove-row! sample-table 1)
    (is (= sample-table
           {:id 0
            :name "tabtab"
            :column-count 1
            :row-count 2
            :columns {0 {:id 0 :name "foo"}
                      1 {:id 1 :name "bar"}}
            :rows {0 {:id 0
                      :values {0 "some text"
                               1 "an other text"}}
                   2 {:id 2
                      :values {}}}}))))

(deftest table-columns-operations
  (let [sample-table (make-sample-table)]
    (add-column! sample-table "boo")
    (is (= sample-table
           {:id 0
            :name "tabtab"
            :column-count 2
            :row-count 1
            :columns {0 {:id 0 :name "foo"}
                      1 {:id 1 :name "bar"}
                      2 {:id 2 :name "boo"}}
            :rows {0 {:id 0
                      :values {0 "some text" ;; col `foo`
                               1 "an other text"}} ;; col `bar`
                   1 {:id 1
                      :values {0 "cell"
                               1 "value"}}}}))
    (remove-column! sample-table 1)
    (is (= sample-table
           {:id 0
            :name "tabtab"
            :column-count 2
            :row-count 1
            :columns {0 {:id 0 :name "foo"}
                      2 {:id 2 :name "boo"}}
            :rows {0 {:id 0
                      :values {0 "some text" ;; col `foo`
                               }} ;; col `bar`
                   1 {:id 1
                      :values {0 "cell"
                               }}}}))))

(ng-test myApp
  (:controller myCtrl
    (:tabular
     (addTwo 1) {:result 3}))

  (:service myService
    (:tabular
     (addThree 1) 4))

  (:filter myFilter
    (:tabular
     [1] 6))

  (:filter yourFilter
    (:tabular
     [2] 8))

  (:directive MyDirective
    (:tabular
     [:div {:my-directive "foo"}]
     {:foo 2}
     ;; Calling $compile function against provided template and scope
     ;; returns an element.
     ;; `(text)` (the same as `text` because they're called by `..` macro)
     ;; is method call of that element.
     ;; These methods are provided by Angular's jQuery lite
     ;; To get full list of them, consult `angular.element` section
     ;; in AngularJS Global APIs.
     "6" text
     "6" (text))))

;; Local Variables:
;; mode: clojure
;; eval: (define-clojure-indent
;;         (ng-test (quote defun))
;;         (:controller (quote defun))
;;         (:service (quote defun))
;;         (:filter (quote defun))
;;         (:directive (quote defun))
;;         (:factory (quote defun)))
;; End:
