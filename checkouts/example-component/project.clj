(defproject example-component "0.1.0-SNAPSHOT"
  :description "Foo bar"
  :source-paths ["src"]
  :resource-paths ^:replace []
  :test-paths ^:replace []
  :target-path "lein-target"
  ;; scope provided makes the dependency non transitive
  ;; apps will define their own clj and cljs versions
  :dependencies [[org.clojure/clojure "1.8.0" :scope "provided"]
                 [org.clojure/clojurescript "1.9.229" :scope "provided"]
                 [reagent "0.6.0-20160714.075816-3"]]

  :profiles {:dev {:source-paths ["dev-src"]
                   :resource-paths ["lein-target/cljs-dev"]
                   :plugins [[lein-figwheel "0.5.7"]
                             [lein-cljsbuild "1.1.4"]]
                   :dependencies [[devcards "0.2.1-7" :exclusions [cljsjs/react]]
                                  [binaryage/devtools "0.8.1"]]}}

  :cljsbuild {:builds [{:id "dev"
                        :source-paths ["src" "dev-src"]
                        :figwheel true
                        :compiler {:main "example.main"
                                   :asset-path "out"
                                   :output-to "lein-target/cljs-dev/public/main.js"
                                   :output-dir "lein-target/cljs-dev/public/out"
                                   :pretty-print true
                                   :closure-defines {"goog.LOCALE" "fi"}
                                   :devcards true
                                   :preloads [devtools.preload]
                                   :external-config {:devtools/config {:features-to-install [:formatters :hints]}}}}]}

  :figwheel {:http-server-root "public"
             :server-port 3450
             :repl true})
