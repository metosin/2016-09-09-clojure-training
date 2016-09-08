(set-env!
  ; Test path can be included here as source-files as they are not included in JAR
  ; Just be careful to not AOT them.
  ; Cljs doesn't need to be included in JAR, resulting JS is included.
  :source-paths #{"src/cljs" "src/less" "test/clj" "test/cljs"}
  ; Resource-paths are included in the JAR
  :resource-paths #{"src/clj" "src/cljc"}
  :dependencies '[[org.clojure/clojure    "1.8.0"]
                  [org.clojure/clojurescript "1.9.229" :scope "provided"]

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

                  [http-kit "2.2.0"]
                  [com.taoensso/sente "1.10.0"]
                  [com.cognitect/transit-clj "0.8.288"]
                  [org.clojure/tools.namespace "0.3.0-alpha3"]
                  [reloaded.repl "0.2.3"]
                  [com.stuartsierra/component "0.3.1"]
                  [metosin/ring-http-response "0.8.0"]
                  [ring/ring-core "1.5.0"]
                  [compojure "1.5.1"]
                  [hiccup "1.0.5"]

                  ; Frontend
                  ;; Latest snapshot fixes some important issues - use the specific version
                  [reagent "0.6.0-20160714.075816-3" :scope "provided"]
                  [example-component "0.1.0-SNAPSHOT" :scope "provided"]
                  [com.cognitect/transit-cljs "0.8.239" :scope "provided"]
                  [binaryage/devtools "0.8.1" :scope "provided"]]
  :checkouts '[[example-component "0.1.0-SNAPSHOT"]])

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
  pom {:project 'training
       :version "0.1.0-SNAPSHOT"
       :description "Foo bar"}
  ;; Component is used by the aot compiled main
  aot {:namespace #{'backend.main 'com.stuartsierra.component 'com.stuartsierra.dependency}}
  jar {:main 'backend.main}
  cljs {:source-map true}
  less {:source-map true})

(deftask dev
  "Start the dev env..."
  []
  (comp
    (watch)
    (less)
    ;; FIXME: Needlesly unintuitive
    (reload :asset-path "/public")
    ; This starts a nrepl server with piggieback middleware
    (cljs-repl)
    (cljs)
    (start-app)))

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

(deftask docker-zip
  [i include INCLUDE edn "Regex to select files in the fileset to package"
   l local-files LOCAL #{str} "Local files to add to the zip"
   o output OUTPUT str "Name for output file"]
  ;; https://github.com/boot-clj/boot/wiki/Tasks#task-anatomy
  ;; FIXME: implement
  identity)

(deftask package
  "Build the package"
  []
  (comp
    (less :compression true)
    (cljs :optimizations :advanced
          :compiler-options {:preloads []})
    (aot)
    (pom)
    ;; Do not included provided deps in the uberjar - slimmer jar
    (uber :exclude-scope #{"provided"})
    (jar :file "app.jar")
    (sift :include #{#"app\.jar$"})
    ;; Package dockerfile and jar for Amazon Beanstalk.
    ;; docker-zip.sh does the same
    (docker-zip :include #{#"app\.jar"} ;; from fileset
                :local-files #{"Dockerfile"}
                :output "app.zip"
                ;; Extra
                ;; :output "app-%1$s.zip" where %1$s is replaced with a timestamp
                )
    (target :dir #{"target"})))
