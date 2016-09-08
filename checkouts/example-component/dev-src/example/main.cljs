(ns example.main
  (:require [devcards.core :as dc :include-macros true]
            example.core-example))

(defn restart! []
  (dc/start-devcard-ui!))

(restart!)

