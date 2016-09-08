(ns backend.main
  "A stub ns which is AoT compiled for the uberjar.

  References to project are resolved dynamically to
  prevent aot compilation."
  (:require reloaded.repl)
  (:gen-class))

(defn init
  "Dynamically create a new system."
  ([] (init nil))
  ([opts]
   (require 'backend.server)
   ((resolve 'backend.server/new-system) opts)))

(defn -main [& args]
  (reloaded.repl/set-init! init)
  (reloaded.repl/go))
