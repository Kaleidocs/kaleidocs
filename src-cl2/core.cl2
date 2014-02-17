(load-file "angular-cl2/src/core.cl2")
(load-file "socket-cl2/src/client.cl2")
(load-file "atom-crud/src/core.cl2")
(load-file "../test-cl2/sample_data.cl2")
(load-file "table_operations.cl2")
(load-file "socket.cl2")


(defapp my-app [ng-route xeditable ng-tags-input angular-file-upload
                ng-grid ui.bootstrap ng-csv])

;; don't have to specify app name as compiler remember the last app name
;; defined in `defapp`
(defroute
  "/history"
  {:controller 'history-ctrl
   :template
   (hiccup
    [:div.gridStyle {:ng-grid "gridOptions"}]
    [:br]
    [:button.btn.btn-primary
     {:type "button"
      :ng-csv "history"
      :filename "history.csv"}
     "Export"]
    )}
  "/debug"
  {:controller 'debug-ctrl
   :template
   (hiccup
    [:h3 "Config"]
    [:div "{{config}}"]
    [:h3 "Profiles"]
    [:div "{{profiles}}"]
    [:h3 "Records"]
    [:div "{{records}}"]
    [:h3 "Tables"]
    [:div "{{tables}}"]
    [:h3 "Templates"]
    [:div "{{templates}}"]
    [:h3 "Produce"]
    [:div "{{produce}}"])}

  "/templates"
  ['templates-ctrl "partials/templates.html"]

  "/profiles"
  ['profiles-ctrl "partials/profiles.html"]

  "/records"
  ['records-ctrl "partials/records.html"]

  "/tables"
  ['tables-ctrl "partials/tables.html"]

  "/produce"
  ['produce-ctrl "partials/produce.html"]

  "/config"
  ['config-ctrl "partials/config.html"]

  :default "/records")

(load-file "ctrls/templates.cl2")
(load-file "ctrls/profiles.cl2")
(load-file "ctrls/records.cl2")
(load-file "ctrls/history.cl2")
(load-file "ctrls/tables.cl2")
(load-file "ctrls/produce.cl2")

(defcontroller config-ctrl
  [$scope]
  ($->atom config config))

(defcontroller navbar-ctrl
  [$scope]
  (def$ save-all save-all)
  (def$ load-all load-all))

(defcontroller debug-ctrl
  [$scope]
  ($->atom tables tables)
  ($->atom profiles profiles)
  ($->atom records records)
  ($->atom templates templates)
  ($->atom produce produce)
  ($->atom config config))

(defcontroller generated-ctrl
  [$scope]
  ($->atom config config)
  ($->atom tables tables)
  )

(defcontroller select-ctrl
  [$scope])

(defcontroller status-ctrl
  [$scope]
  ($->atom status status))

(.run my-app
      (fn-di [editableOptions]
        (set! (:theme editableOptions) "bs3")))

(load-file "utils.cl2")
(load-file "filters.cl2")
