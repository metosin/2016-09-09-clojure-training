(ns frontend.handler
  (:require [frontend.state :as state]))

(defmulti event-msg-handler :id)

(defn event-msg-handler* [{:as ev-msg :keys [id ?data event]}]
  (js/console.log "Event:" event)
  (event-msg-handler ev-msg))

(defmethod event-msg-handler :default
  [{:as ev-msg :keys [event]}]
  (js/console.log "Unhandled event:" event))

(defmethod event-msg-handler :chsk/state
  [{:as ev-msg :keys [?data]}]
  (if (= ?data {:first-open? true})
    (js/console.log "Channel socket successfully established!")
    (js/console.log "Channel socket state change:" ?data)))

(defmethod event-msg-handler :chsk/handshake
  [{:as ev-msg :keys [?data]}]
  (let [[?uid ?csrf-token ?handshake-data] ?data]
    (js/console.log "Handshake:" ?data)
    (state/chsk-send! [:todos.query/list])))

(defmethod event-msg-handler :chsk/recv
  [{:as ev-msg :keys [?data]}]
  (js/console.log "Push event from server:" ?data)
  ;; For some reason all messages from server are handled by this method
  ;; In this example messages are all in format [id data]
  ;; This example calls this same multimethod again, with destructured message
  (if ?data
    (event-msg-handler (assoc ev-msg
                              :id (first ?data)
                              :?data (second ?data)))))

;;
;; App logic
;;

(defmethod event-msg-handler :todos/added
  [{:as ev-msg :keys [?data]}]
  (let [{:keys [id]} ?data]
    (swap! state/todos assoc id ?data)))

(defmethod event-msg-handler :todos/update
  [{:as ev-msg :keys [?data]}]
  (let [{:keys [id]} ?data]
    (swap! state/todos update id merge ?data)))

(defmethod event-msg-handler :todos/removed
  [{:as ev-msg :keys [?data]}]
  (let [{:keys [id]} ?data]
    (swap! state/todos dissoc id)))

(defmethod event-msg-handler :todos/list
  [{:as ev-msg :keys [?data]}]
  (reset! state/todos (into {} (map (juxt :id identity) ?data))))
