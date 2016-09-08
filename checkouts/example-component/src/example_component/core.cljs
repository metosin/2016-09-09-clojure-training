(ns example-component.core
  (:require [reagent.core :as r]))

(def container-style
  {:position :relative})

(def dropdown-style
  {:position :absolute
   :top "100%"
   :margin 0
   :background "white"
   :border "1px solid #000"})

(defn autocomplete [_]
  (let [open? (r/atom false)]
    (fn [{:keys [container-class dropdown-class dropdown-item-class
                 value on-change on-focus on-blur]
          :as props}]
      [:div
       {:style container-style
        :class container-class}
       [:input
        (-> props
            (dissoc :container-class :dropdown-class :dropdown-item-class)
            (assoc :value value
                   :on-change on-change
                   :on-focus (fn [e]
                               (reset! open? true)
                               (if on-focus (on-focus e)))
                   :on-blur (fn [e]
                              (reset! open? false)
                              (if on-blur (on-blur e)))))]
       (if @open?
         [:div
          {:style dropdown-style
           :class dropdown-class}
          [:div "foo"]
          [:div "Some items"]])])))
