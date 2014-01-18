(load-file "core.cl2")

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
