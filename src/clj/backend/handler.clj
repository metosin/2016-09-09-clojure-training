(ns backend.handler
  (:require [backend.state :as state]))

(defn broadcast [ev-msg message]
  (doseq [uid (:any @(:connected-uids ev-msg))]
    ((:send-fn ev-msg) uid message)))

(defmulti event-msg-handler :id)

(defn event-msg-handler* [{:as ev-msg :keys [id ?data event]}]
  (println "Event:" event)
  (event-msg-handler ev-msg))

(defmethod event-msg-handler :default
  [{:as ev-msg :keys [event id ?data ring-req ?reply-fn send-fn]}]
  (let [session (:session ring-req)
        uid     (:uid     session)]
    (println "Unhandled event:" event)
    (when ?reply-fn
      (?reply-fn {:umatched-event-as-echoed-from-from-server event}))))

(defmethod event-msg-handler :todos.command/add
  [{:as ev-msg :keys [?data]}]
  (let [id (inc (if (seq @state/todos)
                  (apply max (map :id (vals @state/todos)))
                  0))
        data (assoc ?data :id id)]
    (swap! state/todos assoc id data)
    (broadcast ev-msg [:todos/added data])))

(defmethod event-msg-handler :todos.command/toggle
  [{:as ev-msg :keys [?data]}]
  (let [{:keys [id]} ?data]
    (swap! state/todos update-in [id :done] not)
    (broadcast ev-msg [:todos/update (get @state/todos id)])))

(defmethod event-msg-handler :todos.command/delete
  [{:as ev-msg :keys [?data]}]
  (let [{:keys [id]} ?data]
    (swap! state/todos dissoc id)
    (broadcast ev-msg [:todos/removed {:id id}])))

(defmethod event-msg-handler :todos.command/save
  [{:as ev-msg :keys [?data]}]
  (let [{:keys [id]} ?data]
    (swap! state/todos update id merge (dissoc ?data :id))
    (broadcast ev-msg [:todos/update (get @state/todos id)])))

(defmethod event-msg-handler :todos.query/list
  [{:as ev-msg :keys [?data ?reply-fn]}]
  (let [{:keys [id]} ?data]
    (?reply-fn [:todos/list (vals @state/todos)])))
