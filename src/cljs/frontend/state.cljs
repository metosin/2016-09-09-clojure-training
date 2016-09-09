(ns frontend.state
  (:require [reagent.core :as r]
            [taoensso.sente :as sente]
            [taoensso.sente.packers.transit :as sente-transit]))

(defonce todos (r/atom (sorted-map)))

(defonce counter (r/atom 0))

(let [packer (sente-transit/get-transit-packer)
      socket (sente/make-channel-socket! "/chsk" {:type :auto
                                                  :packer packer})]
  (def chsk (:chsk socket))
  (def ch-chsk (:ch-recv socket))
  (def chsk-send! (:send-fn socket))
  (def chsk-state (:state socket)))
