(set-env!
  ; Test path can be included here as source-files as they are not included in JAR
  ; Just be careful to not AOT them.
  ; Cljs doesn't need to be included in JAR, resulting JS is included.
  :source-paths #{"src/cljs" "src/less" "test/clj" "test/cljs"}
  ; Resource-paths are included in the JAR
  :resource-paths #{"src/clj" "src/cljc"}
  :dependencies '[[org.clojure/clojure    "1.9.0-alpha11"]
                  [org.clojure/clojurescript "1.9.229"]

                  [boot/core              "2.6.0"      :scope "test"]
                  [adzerk/boot-cljs       "1.7.228-1"  :scope "test"]
                  [adzerk/boot-cljs-repl  "0.3.3"      :scope "test"]
                  [crisptrutski/boot-cljs-test "0.2.2-SNAPSHOT" :scope "test"]
                  [com.cemerick/piggieback "0.2.1"     :scope "test"]
                  [weasel                 "0.7.0"      :scope "test"]
                  [org.clojure/tools.nrepl "0.2.12"    :scope "test"]
                  [adzerk/boot-reload     "0.4.12"     :scope "test"]
                  [metosin/boot-alt-test  "0.1.2"      :scope "test"]
                  [deraen/boot-less       "0.5.0"      :scope "test"]
                  ;; For boot-less
                  [org.slf4j/slf4j-nop    "1.7.21"     :scope "test"]

                  ; Backend
                  [http-kit "2.2.0"]
                  [org.clojure/tools.namespace "0.3.0-alpha3"]
                  [reloaded.repl "0.2.2"]
                  [com.stuartsierra/component "0.3.1"]
                  [metosin/ring-http-response "0.8.0"]
                  [ring "1.5.0"]
                  [compojure "1.5.1"]
                  [hiccup "1.0.5"]

                  ; Frontend
                  [reagent "0.6.0-SNAPSHOT"]
                  [binaryage/devtools "0.8.1"]])

(require
  '[adzerk.boot-cljs      :refer [cljs]]
  '[adzerk.boot-cljs-repl :refer [cljs-repl start-repl repl-env]]
  '[adzerk.boot-reload    :refer [reload]]
  '[metosin.boot-alt-test  :refer [alt-test]]
  '[deraen.boot-less      :refer [less]]
  '[crisptrutski.boot-cljs-test :refer [test-cljs prep-cljs-tests run-cljs-tests]]
  '[backend.boot          :refer [start-app]]
  '[reloaded.repl         :refer [go reset start stop system]])

(task-options!
  pom {:project 'saapas
       :version "0.1.0-SNAPSHOT"
       :description "Application template for Cljs/Om with live reloading, using Boot."
       :license {"The MIT License (MIT)" "http://opensource.org/licenses/mit-license.php"}}
  aot {:namespace #{'backend.main}}
  jar {:main 'backend.main}
  cljs {:source-map true}
  less {:source-map true})

(deftask dev
  "Start the dev env..."
  [p port       PORT int  "Port for web server"]
  (comp
    (watch)
    (less)
    (reload :ids #{"js/main"})
    ; This starts a repl server with piggieback middleware
    (cljs-repl :ids #{"js/main"})
    (cljs :ids #{"js/main"})
    (start-app :port port)))

(deftask run-tests
  [a autotest bool "If no exception should be thrown when tests fail"]
  (comp
    (alt-test :fail (not autotest))
    ;; FIXME: This is not a good place to define which namespaces to test
    (test-cljs :namespaces #{"frontend.core-test"})))

(deftask autotest []
  (comp
    (watch)
    (run-tests :autotest true)))

(deftask package
  "Build the package"
  []
  (comp
    (less :compression true)
    (cljs :optimizations :advanced)
    (aot)
    (pom)
    (uber)
    (jar)
    (target)))