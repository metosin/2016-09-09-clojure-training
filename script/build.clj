(require 'cljs.build.api)

(cljs.build.api/build
  (cljs.build.api/inputs "src/cljs")
  {:main "frontend.main"
   :output-to "out/main.js"
   :output-dir "out"
   :optimizations :none
   :pretty-print true
   :compiler-stats true})
