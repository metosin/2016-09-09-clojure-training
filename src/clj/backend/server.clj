(ns backend.server
  (:require [com.stuartsierra.component :as component]
            [compojure.core :refer [GET POST routes]]
            [compojure.route :refer [resources]]
            [ring.util.http-response :refer :all]
            [ring.middleware.keyword-params :refer [wrap-keyword-params]]
            [ring.middleware.params :refer [wrap-params]]
            [org.httpkit.server :refer [run-server]]
            [backend.index :refer [index-page test-page]]
            [taoensso.sente :as sente]
            [taoensso.sente.packers.transit :as sente-transit]
            [taoensso.sente.server-adapters.http-kit :refer [get-sch-adapter]]
            [backend.handler :as handler]))

(defn create-routes [sente]
  (let [{:keys [ajax-post-fn ajax-get-or-ws-handshake-fn]} sente]
    (-> (routes
          (resources "/" {:root "public"})

          (GET "/chsk" req (ajax-get-or-ws-handshake-fn req))
          (POST "/chsk" req (ajax-post-fn req))

          (GET "/" []
            (-> (ok index-page) (content-type "text/html")))
          (GET "/test" []
            (-> (ok test-page) (content-type "text/html"))))

        wrap-keyword-params
        wrap-params)))

;; Component to handle start/stop/reset
;; Alternatives: Mount, DIY...

(defrecord HttpKit [port sente]
  component/Lifecycle
  (start [this]
    (let [port (or port 9000)]
      (println (str "Starting web server on http://localhost:" port))
      (assoc this :http-kit (run-server (create-routes (:socket sente))
                                        {:port port :join? false}))))
  (stop [this]
    (if-let [http-kit (:http-kit this)]
      (http-kit))
    (assoc this :http-kit nil)))

(defrecord Sente [socket]
  component/Lifecycle
  (start [this]
    (let [packer (sente-transit/get-transit-packer)
          {:keys [ch-recv] :as socket} (sente/make-channel-socket! (get-sch-adapter) {:packer packer})
          router (sente/start-chsk-router! ch-recv handler/event-msg-handler*)]
      (assoc this :socket socket :router router)))
  (stop [this]
    (when-let [r (:router this)] (r))
    this))

(defn new-system [opts]
  (component/system-map
    :sente (map->Sente {})
    :http-kit (component/using (map->HttpKit opts) [:sente])))





;; FIXME: c.t.n + boot-cljs + some cljc libraries have current problems when ns with protocol defs are reloaded
(doall (map #(clojure.tools.namespace.repl/disable-reload! (find-ns %)) '[taoensso.sente.interfaces]))
