(ns example.core-example
  (:require [reagent.core :as r]
            [devcards.core :as dc :include-macros true]
            [example-component.core :as core]))

(dc/defcard-rg foo-bar
  "Description"
  (fn [value _]
    [:div
     [core/example
      {:value @value
       :on-change #(reset! value %)}]])
  (r/atom "Hello World")
  {:inspect-data true})
