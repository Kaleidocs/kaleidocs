#_[:input {:type "file" :ng-file-select "onFileSelect($files)"}]
[:div.jumbotron
 {:ng-file-drop "onFileSelect($files)"
  :ng-show "dropSupported"}
 "Drop files here"]
[:div.jumbotron
 {:ng-file-drop-available "dropSupported=true"
  :ng-show "!dropSupported"}
 "HTML5 Drop File is not supported!"]
#_[:div {:ng-click "upload.abort()"}
 "Cancel Upload"]
[:div {:ng-repeat "template in templates"}
 [:button.btn.btn-danger.btn-xs.pull-right
  {:ng-click "removeTemplate(template.id, template.filename)"
   :ng-hide "textBtnForm.$visible"}
  [:span.glyphicon.glyphicon-remove-circle]
  "Delete"]

 [:h3 "{{ template.filename }}"]
 [:select {:ng-model "template.type"
           :ng-options "type for type in ['multiple', 'single']"}
  [:option {:value ""} "-- choose template type --"]]]
