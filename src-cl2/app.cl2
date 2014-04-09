(load-file "angular-cl2/src/core.cl2")
(load-file "socket-cl2/src/client.cl2")
(load-file "atom-crud/src/core.cl2")

(defapp my-app [ng-route angular-file-upload mgcrea.ng-strap
                ng-table ng-resource])

(.config my-app
 (fn-di
  [$modal-provider]
  ;; some problem with html unsafe
  ;; https://github.com/mgcrea/angular-strap/issues/369
  (. angular (extend (-> $modalProvider :defaults) {:html false}))))
#_
(defcontroller testbed-ctrl
  [$scope]
  (def$ modal
    {:content "Hello Modal<br />This is a multiline message! Have fun",
     :title "Title"}))

(defcontroller testbed-ctrl
  [$scope $filter ng-table-params $resource $timeout]
  (def API ($resource "/testbed"))
  (def$ filter-dict {})
  (. $scope
     ($watch "filterDict"
             (fn []
               (if (and (-> $scope :filter-dict)
                        (-> $scope :table-params))
                 (.. (-> $scope :table-params) reload)))
             true))

  (def$ table-params
    (ngTableParams.
     {:count 10, :page 1}
     {:getData
      (fn [$defer params]
        (.get API
              (. params url)
              (fn [data]
                (def filtered-data
                  (($filter "filter") data ($- filter-dict)))
                (def sorted-data
                  (if(.. params sorting)
                    (($filter "orderBy") filtered-data (. params orderBy))
                    filtered-data))
                ($timeout
                 (fn []
                   (. params (total (-> data :total)))
                   (. $defer (resolve (-> sorted-data :result))))
                 500))))}))
  (def$ edit-id -1)
  (defn$ set-edit-id
    [pid]
    (def$ edit-id pid)))

(defroute
  "/testbed"
  {:controller 'testbed-ctrl
   :template
   (hiccup
    [:table.table
     {:show-filter "true", :ng-table "tableParams"}
     [:thead
      [:tr
       [:th.text-center.sortable
        {:ng-click
         "tableParams.sorting(key, tableParams.isSortBy(key, 'asc') ? 'desc' : 'asc')",
         :ng-class
         "{'sort-asc': tableParams.isSortBy(key, 'asc'),
           'sort-desc': tableParams.isSortBy(key, 'desc')}",
         :ng-repeat "key in ['id', 'fn', 'ln', 'em', 'dc', 'ph']"}
        "{{key}}"]]
      [:tr
       [:th.text-center.sortable
        {:ng-repeat "key in ['id', 'fn', 'ln', 'em', 'dc', 'ph']"}
        [:input.form-control
         {:ng-model "filterDict[key]",
          :type "text"}]]]]

     [:tbody
      {:ng-repeat "p in $data"}
      [:tr
       {:id "tr{{p.id}}"
        :ng-class-even "'even'", :ng-class-odd "'odd'"}
       [:td.rowTd
        {:ng-repeat "key in ['id', 'fn', 'ln', 'em', 'dc', 'ph']"
         ;;:filter "{ 'fn': 'text' }",
         :sortable "key",
         :data-title "key"}
        "{{p[key]}}"]
       [:td.rowTd
        [:input
         {:ng-click "setEditId(p.id)", :value "edit", :type "button"
          :id "editRowBtn{{p.id}}"}]]]
      [:tr
       {:ng-if "editId===p.id", :ng-show "editId===p.id"}
       [:td
        {:ng-include "'edit-row.html'",
         :colspan "7"}]]]]

    #_
    [:form
     [:button.btn.btn-lg.btn-primary
      {:bs-modal "modal",
       :data-placement "center",
       :data-animation "am-fade-and-scale",
       :type "button"}
      "Click to toggle modal\n  "
      [:br]
      [:small "(using an object)"]]])}
  :default "/testbed")

(def data
  [{"dl" false,
    "ac" true,
    "ph" "617-321-4567",
    "em" "j.smith@company.com",
    "dc" "CEO",
    "fn" "John",
    "ln" "Smith",
    "lm" 138661285100,
    "id" 1}
   {"dl" false,
    "ac" true,
    "ph" "617-522-5588",
    "em" "l.taylor@company.com",
    "dc" "VP of Marketing",
    "fn" "Lisa",
    "ln" "Taylor",
    "lm" 138661285200,
    "id" 2}
   {"dl" false,
    "ac" true,
    "ph" "617-589-9977",
    "em" "j.jones@company.com",
    "dc" "VP of Sales",
    "fn" "James",
    "ln" "Jones",
    "lm" 138661285300,
    "id" 3}
   {"dl" false,
    "ac" true,
    "ph" "617-245-9785",
    "em" "p.wong@company.com",
    "dc" "VP of Engineering",
    "fn" "Paul",
    "ln" "Wong",
    "lm" 138661285400,
    "id" 4}
   {"dl" false,
    "ac" true,
    "ph" "617-244-1177",
    "em" "a.king@company.com",
    "dc" "Architect",
    "fn" "Alice",
    "ln" "King",
    "lm" 138661285500,
    "id" 5}
   {"dl" false,
    "ac" true,
    "ph" "617-568-9863",
    "em" "j.brown@company.com",
    "dc" "Software Engineer",
    "fn" "Jan",
    "ln" "Brown",
    "lm" 138661285600,
    "id" 6}
   {"dl" false,
    "ac" true,
    "ph" "617-327-9966",
    "em" "a.garcia@company.com",
    "dc" "Software Engineer",
    "fn" "Ami",
    "ln" "Garcia",
    "lm" 138661285700,
    "id" 7}
   {"dl" false,
    "ac" true,
    "ph" "617-565-9966",
    "em" "j.green@company.com",
    "dc" "Software Engineer",
    "fn" "Jack",
    "ln" "Green",
    "lm" 138661285800,
    "id" 8}
   {"dl" false,
    "ac" true,
    "ph" "617-523-4468",
    "em" "a.liesen@company.com",
    "dc" "Plumber",
    "fn" "Abraham",
    "ln" "Liesen",
    "lm" 138661285900,
    "id" 9}
   {"dl" false,
    "ac" true,
    "ph" "617-877-3434",
    "em" "a.bower@company.com",
    "dc" "Product Manager",
    "fn" "Angela",
    "ln" "Bower",
    "lm" 138661286000,
    "id" 10}
   {"dl" false,
    "ac" true,
    "ph" "617-446-9999",
    "em" "f.davidoff@company.com",
    "dc" "Database Admin",
    "fn" "Fjodor",
    "ln" "Davidoff",
    "lm" 138661286100,
    "id" 11}
   {"dl" false,
    "ac" true,
    "ph" "617-111-1111",
    "em" "b.vitrovic@company.com",
    "dc" "Director of Communications",
    "fn" "Biljana",
    "ln" "Vitrovic",
    "lm" 138661286200,
    "id" 12}
   {"dl" false,
    "ac" true,
    "ph" "617-565-4412",
    "em" "g.valet@company.com",
    "dc" "Software Engineer",
    "fn" "Guillaume",
    "ln" "Valet",
    "lm" 138661286300,
    "id" 13}
   {"dl" false,
    "ac" true,
    "ph" "617-866-2554",
    "em" "m.tran@company.com",
    "dc" "Gui Designer",
    "fn" "Min",
    "ln" "Tran",
    "lm" 138661286400,
    "id" 14}])
