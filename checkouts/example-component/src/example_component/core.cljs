(ns example-component.core
  (:require [reagent.core :as r]))

(defn example [{:keys [value on-change]}]
  [:div
   [:p "a component, with a value: " value]
   [:button
    {:type "button"
     :on-click (fn [_]
                 (if on-change
                   (on-change (str value " foo"))))}
    "Change"]])
