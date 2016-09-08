(ns backend.server
  (:require [com.stuartsierra.component :as component]
            [compojure.core :refer [GET defroutes]]
            [compojure.route :refer [resources]]
            [ring.util.http-response :refer :all]
            [org.httpkit.server :refer [run-server]]
            [backend.index :refer [index-page test-page]]))

(defroutes routes
  (resources "/" {:root "public"})

  (GET "/" []
    (-> (ok index-page) (content-type "text/html")))
  (GET "/test" []
    (-> (ok test-page) (content-type "text/html"))))

;; Component to handle start/stop/reset
;; Alternatives: Mount, DIY...

(defrecord HttpKit [port]
  component/Lifecycle
  (start [this]
    (let [port (or port 9000)]
      (println (str "Starting web server on http://localhost:" port))
      (assoc this :http-kit (run-server #'backend.server/routes
                                        {:port port :join? false}))))
  (stop [this]
    (if-let [http-kit (:http-kit this)]
      (http-kit))
    (assoc this :http-kit nil)))

(defn new-system [opts]
  (component/system-map
    :http-kit (map->HttpKit opts)))
