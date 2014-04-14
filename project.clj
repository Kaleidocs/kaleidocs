(defproject kaleidocs "0.1.0-SNAPSHOT"
  :description "An Angular web app designed to ... well, that part is up to you."
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [http-kit "2.1.16"]
                 [lib-noir "0.7.8"]
                 [lobos "1.0.0-beta1"]
                 [com.taoensso/timbre "2.7.1"]
                 [ring/ring-devel "1.1.8"]
                 [ring/ring-json "0.3.0"]
                 [kaleidocs/meld "0.4.0"]
                 [korma "0.3.0-RC5"]
                 [org.clojure/java.jdbc "0.2.3"]
                 [com.h2database/h2 "1.3.176"]
                 [n2w-vi "0.1.0"]
                 [org.clojars.hozumi/clj-commons-exec "1.0.7"]]
  :profiles
  {:deploy
   {:uberjar-name "kaleidocs.jar"
    :main kaleidocs.core
    :aot :all
    :cl2c {:frontend
           {:watch ["src-cl2", "test-cl2"]
            :filter (or "src-cl2/app.cl2"
                        (and "src-cl2" ".hic$"))
            :path-map ["src-cl2/" => "resources/public/"]
            :paths ["node_modules/"]
            :strategy "prod"
            :optimizations :simple
            ;; some files may take too long to compile. We need a limit
            :timeout 2000}}}
   :dev
   {:source-paths ["dev"]
    :dependencies
    [[org.clojure/tools.namespace "0.2.4"]
     [fr.opensagres.xdocreport/fr.opensagres.xdocreport.document.odt "1.0.3"]
     [fr.opensagres.xdocreport/fr.opensagres.xdocreport.document.ods "1.0.3"]]
    :cl2c {:frontend {:optimizations :pretty
                      :strategy "dev"
                      :watch ["src-cl2", "test-cl2"]
                      :filter (or "src-cl2/app.cl2"
                                  (and "src-cl2" ".hic$"))
                      :path-map ["src-cl2/" => "resources/public/"]
                      :paths ["node_modules/" "src-cl2/"]
                      ;; some files may take too long to compile. We need a limit
                      :timeout 2000}}}}
  :node-dependencies [[atom-crud "0.1.0-SNAPSHOT"]]
  :nodejs {:keywords ["chlorinejs",
                      "clojure",
                      "macro",
                      "angular",
                      "qunit",
                      "bootstrap"]
           :devDependencies {:angular-cl2 "0.4.0-SNAPSHOT",
                             :less "~1.4.0"
                             :testem "0.6.2"
                             :socket-cl2 "0.2.0"}
           :scripts {:testem "testem ci"}}
  :plugins [[lein-cl2c "0.0.1-SNAPSHOT"]
            [lein-bower "0.4.0"]
            [lein-npm "0.4.0"]]
  :bower {:directory "resources/public/vendor"}
  :bower-dependencies [[sockjs "~0.3.4"]
                       [angular-bootstrap "0.10.0"]
                       [ng-table "~0.3.1"]
                       [angular-route "~1.2.16"]
                       [angular-resource "~1.2.16"]
                       [angular-sanitize "~1.2.16"]
                       [angular-i18n "~1.2.16"]
                       [ng-file-upload "~1.2.11"]]
  )
