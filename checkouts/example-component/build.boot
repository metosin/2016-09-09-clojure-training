(set-env!
  :source-paths #{"dev-src"}
  :resource-paths #{"src"}
  :dependencies '[[org.clojure/clojure    "1.8.0" :scope "provided"]
                  [org.clojure/clojurescript "1.9.229" :scope "provided"]

                  [boot/core              "2.6.0"      :scope "test"]
                  [adzerk/boot-cljs       "1.7.228-1"  :scope "test"]
                  [adzerk/boot-cljs-repl  "0.3.3"      :scope "test"]
                  [crisptrutski/boot-cljs-test "0.2.2-SNAPSHOT" :scope "test"]
                  [com.cemerick/piggieback "0.2.1"     :scope "test"]
                  [weasel                 "0.7.0"      :scope "test"]
                  [org.clojure/tools.nrepl "0.2.12"    :scope "test"]
                  [adzerk/boot-reload     "0.4.12"     :scope "test"]
                  [pandeiro/boot-http     "0.7.3"      :scope "test"]

                  [reagent "0.6.0-20160714.075816-3"]

                  [binaryage/devtools "0.8.1" :scope "test"]
                  [devcards "0.2.1-7" :scope "test" :exclusions [cljsjs/react]]])

(require
  '[adzerk.boot-cljs      :refer [cljs]]
  '[adzerk.boot-cljs-repl :refer [cljs-repl start-repl repl-env]]
  '[adzerk.boot-reload    :refer [reload]]
  '[crisptrutski.boot-cljs-test :refer [test-cljs prep-cljs-tests run-cljs-tests]]
  '[pandeiro.boot-http    :refer [serve]])

(task-options!
  pom {:project 'example-component
       :version "0.1.0-SNAPSHOT"
       :description "Foo bar"}
  cljs {:source-map true})

(deftask build
  "Install the lib to local maven repo"
  []
  (comp
    ;; FIXME: Compiled files etc. should not be included in JAR, even from dev task
    ; (sift :include #{#"^example_component"})
    (pom)
    (jar)
    (install)))

(deftask dev []
  (comp
    (watch)
    (reload)
    (cljs-repl)
    (cljs)
    (serve :resource-root "public")
    (build)))
