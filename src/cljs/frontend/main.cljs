(ns frontend.main
  (:require [reagent.core :as r]
            [example-component.core :refer [autocomplete]]))

;; Based on https://github.com/holmsand/reagent/blob/master/examples/todomvc/src/todomvc/core.cljs

(defonce todos (r/atom (sorted-map)))

(defonce counter (r/atom 0))

(defn add-todo [text]
  (let [id (swap! counter inc)]
    (swap! todos assoc id {:id id :title text :done false})))

(defn toggle [id] (swap! todos update-in [id :done] not))
(defn save [id title] (swap! todos assoc-in [id :title] title))
(defn delete [id] (swap! todos dissoc id))

(defn mmap [m f a] (->> m (f a) (into (empty m))))
(defn complete-all [v] (swap! todos mmap map #(assoc-in % [1 :done] v)))
(defn clear-done [] (swap! todos mmap remove #(get-in % [1 :done])))

(defonce init (do
                (add-todo "Write CSS for this app")
                (add-todo "Write Sente + Transit code")
                (add-todo "Think about reusable Reagent component")
                (add-todo "Write code for Boot Docker zip task")
                (complete-all true)))

(defn todo-input [{:keys [title on-save on-stop focus-on-mount]}]
  (let [val (r/atom title)
        stop #(do (reset! val "")
                  (if on-stop (on-stop)))
        save #(let [v (-> @val str clojure.string/trim)]
                (if-not (empty? v) (on-save v))
                (stop))]
    (fn [{:keys [title on-save on-stop focus-on-mount] :as props}]
      [autocomplete
       (-> props
           (dissoc :title :on-stop :on-save :focus-on-mount)
           (merge {:type "text"
                   :value @val
                   :on-blur save
                   :auto-focus focus-on-mount
                   :container-class "todo-input__container"
                   :on-change #(reset! val (-> % .-target .-value))
                   :on-key-down #(case (.-which %)
                                   13 (save) ;; Enter
                                   27 (stop) ;; Esc
                                   nil)}))])))

(defn todo-item []
  (let [editing (r/atom false)]
    (fn [{:keys [id done title]}]
      [:li.todo-list__item.todo-item
       {:class (str (if done "todo-item--completed ")
                    (if @editing "todo-item--editing"))}
       [:input.todo-item__toggle
        {:type "checkbox"
         :checked done
         :on-change #(toggle id)
         :id (str "checkbox_" id)}]
       (if @editing
         [todo-input
          {:class "todo-item__input"
           :title title
           :on-save #(save id %)
           :on-stop #(reset! editing false)
           :focus-on-mount true}]
         [:label.todo-item__label
          {:for (str "checkbox_" id)} title])
       [:button.todo-item__edit
        {:on-click #(reset! editing true)}]
       [:button.todo-item__destroy
        {:on-click #(delete id)}]])))

(defn todo-app [props]
  (let [filt (r/atom :all)]
    (fn []
      (let [items (vals @todos)
            done (->> items (filter :done) count)
            active (- (count items) done)]
        [:div.todo-app
         [:header.todo-app__header
          [:h1 "todos"]

          [todo-input
           {:id "new-todo"
            :placeholder "What needs to be done?"
            :on-save add-todo
            :class "new-todo-input"}]]

         (when (-> items count pos?)
           [:ul.todo-list
            (for [todo (filter (case @filt
                                 :active (complement :done)
                                 :done :done
                                 :all identity) items)]
              ^{:key (:id todo)}
              [todo-item todo])])

         (when (pos? done)
           [:button.clear-done-button
            {:on-click clear-done}
            "Clear completed " done])

         ]))))

(defn start! []
  (js/console.log "Starting the app")
  (r/render-component [todo-app] (js/document.getElementById "app")))

;; When this namespace is (re)loaded the Reagent app is mounted to DOM
(start!)

(comment
  (println "foo"))
