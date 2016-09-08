(defproject training "0.1.0-SNAPSHOT"
  :description "Foo bar"
  :source-paths ["src/cljs" "src/clj" "src/cljc"]
  :resource-paths ^:replace []
  :test-paths ["test/clj"]
  :target-path "lein-target"
  :dependencies [[org.clojure/clojure "1.8.0"]

                 [http-kit "2.2.0"]
                 [com.taoensso/sente "1.10.0"]
                 [org.clojure/tools.namespace "0.3.0-alpha3"]
                 [reloaded.repl "0.2.3"]
                 [com.stuartsierra/component "0.3.1"]
                 [metosin/ring-http-response "0.8.0"]
                 [ring/ring-core "1.5.0"]
                 [compojure "1.5.1"]
                 [hiccup "1.0.5"]]
  :main backend.main
  :repl-options {:init-ns user}

  :profiles {;; These dependencies should not be included in the uberjar
             :cljs {:dependencies [[org.clojure/clojurescript "1.9.229"]
                                   [reagent "0.6.0-20160714.075816-3"]
                                   [example-component "0.1.0-SNAPSHOT"]
                                   [binaryage/devtools "0.8.1"]]}

             :uberjar {:uberjar-name "app.jar"
                       :auto-clean false
                       :resource-paths ^:replace ["lein-target/cljs-prod" "lein-target/less"]
                       :target-path "target"
                       :clean-targets ["lein-target" "target"]
                       ;; Component is used by the aot compiled main
                       :aot [backend.main com.stuartsierra.component com.stuartsierra.dependency]}

             :dev [{:resource-paths ["lein-target/less" "lein-target/cljs-dev"]
                     :plugins [[lein-pdo "0.1.1"]
                               [lein-figwheel "0.5.7"]
                               [lein-cljsbuild "1.1.4"]
                               [deraen/lein-less4j "0.5.0"]]
                     :dependencies [;; For lein-less4j
                                    [org.slf4j/slf4j-nop    "1.7.21"     :scope "test"]]}
                   :cljs]}

  :cljsbuild {:builds [{:id "dev"
                        ;; only needs to include dir with the main file, others can be read from classpath
                        ;; Figwheel needs checkout paths here for auto compilete (https://github.com/bhauman/lein-figwheel/issues/9)
                        :source-paths ["src/cljs" "checkouts/example-component/src"]
                        :figwheel true
                        :compiler {:main "frontend.main"
                                   :asset-path "js/out"
                                   :output-to "lein-target/cljs-dev/public/js/main.js"
                                   :output-dir "lein-target/cljs-dev/public/js/out"
                                   :pretty-print true
                                   :closure-defines {"goog.LOCALE" "fi"}
                                   :preloads [devtools.preload]
                                   :external-config {:devtools/config {:features-to-install [:formatters :hints]}}}}

                       {:id "prod"
                        :source-paths ["src/cljs"]
                        :compiler {:main "frontend.main"
                                   :output-to "lein-target/cljs-prod/public/js/main.js"
                                   :output-dir "lein-target/cljs-prod/public/js/main.out"
                                   :source-map "lein-target/cljs-prod/public/js/main.js.map"
                                   :optimizations :advanced
                                   :parallel-build true
                                   :pretty-print false
                                   :closure-defines {"goog.LOCALE" "fi"}}}]}

  :figwheel {:http-server-root "public"
             :server-port 3450
             :css-dirs ["lein-target/less"]
             :repl true}

  :less {:source-paths ["src/less"]
         :target-path "lein-target/less"
         :source-map true}

  :aliases {"build" ["with-profile" "uberjar" "do"
                     ["clean"]
                     ["with-profile" "+dev" ["less4j" "once"]]
                     ["with-profile" "+cljs" ["cljsbuild" "once" "prod"]]
                     ["uberjar"]]}

  ;; Disable deployment
  :deploy-repositories [["clojars" ^:replace {:password "invalid" :username "invalid" :url "invalid"}]])
