(ns user
  (:require [reloaded.repl :refer [go stop reset system]]
            backend.main))

(reloaded.repl/set-init! backend.main/init)
