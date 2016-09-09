(ns backend.handler
  (:require [backend.state :as state]))

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

(defmethod event-msg-handler :todos/add
  [{:keys [?data send-fn ?reply-fn connected-uids]}]
  (let [id (str (java.util.UUID/randomUUID))
        data (assoc ?data :id id)]
    ;; Broadcast
    (doseq [uid (:any @connected-uids)]
      (send-fn uid [:todos/added data]))
    (when ?reply-fn
      (?reply-fn [:todos/added data]))
    (swap! state/todos assoc id data)))
