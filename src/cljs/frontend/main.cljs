(ns frontend.main
  (:require [reagent.core :as r]
            [taoensso.sente :as sente]
            [example-component.core :refer [autocomplete]]
            [frontend.state :as state]
            [frontend.handler :as handler]))

;; Based on https://github.com/holmsand/reagent/blob/master/examples/todomvc/src/todomvc/core.cljs

(defn add-todo [text]
  (state/chsk-send! [:todos.command/add {:title text :done false}]))

(defn toggle [id]
  (state/chsk-send! [:todos.command/toggle {:id id}]))

(defn save [id title]
  (state/chsk-send! [:todos.command/save {:id id :title title}]))

(defn delete [id]
  (state/chsk-send! [:todos.command/delete {:id id}]))

(defn mmap [m f a] (->> m (f a) (into (empty m))))
(defn complete-all [v] (swap! state/todos mmap map #(assoc-in % [1 :done] v)))
(defn clear-done [] (swap! state/todos mmap remove #(get-in % [1 :done])))

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
      (let [items (vals @state/todos)
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

(defonce router (atom nil))

(defn start! []
  (js/console.log "Starting the app")
  (swap! router (fn [old]
                  (if old (old))
                  (sente/start-chsk-router! state/ch-chsk handler/event-msg-handler*)))
  (r/render-component [todo-app] (js/document.getElementById "app")))

;; When this namespace is (re)loaded the Reagent app is mounted to DOM
(start!)

(comment
  (println "foo"))
