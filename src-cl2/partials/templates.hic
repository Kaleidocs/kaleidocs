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
