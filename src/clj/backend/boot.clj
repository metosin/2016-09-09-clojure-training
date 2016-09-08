(ns backend.boot
  {:boot/export-tasks true}
  (:require [boot.core :as b]
            reloaded.repl
            backend.main
            [clojure.tools.namespace.repl :refer [disable-reload!]]))

(disable-reload!)

(b/deftask start-app []
  (let [x (atom nil)]
    (fn middleware [next-handler]
      (fn handler [fileset]
        (swap! x (fn [x]
                   (if x
                     x
                     (do (reloaded.repl/set-init! backend.main/init)
                         (reloaded.repl/go)))))
        fileset))))
