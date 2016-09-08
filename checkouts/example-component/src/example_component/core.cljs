(ns example-component.core
  (:require [reagent.core :as r]))

(defn autocomplete [{:keys [value on-change]}]
  [:input
   {:value value
    :on-change on-change}])
