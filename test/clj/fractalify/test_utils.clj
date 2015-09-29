(ns fractalify.test-utils
  (:require [fractalify.dev :as dev]
            [com.stuartsierra.component :as c]
            [fractalify.system :as sys]
            [fractalify.mailers.mock-mail-sender :as mms]
            [fractalify.config :as cfg]))

(defn init-test-system []
  (let [deps (merge sys/db-generators-dependencies (sys/new-dependency-map))]
    (-> (sys/new-system-map (merge (cfg/config) {:http-listener {:port 10556}}))
        (assoc :mail-sender (mms/new-mock-mail-sender))
        (sys/generator-components nil)
        (c/system-using deps)
        (dev/init))))

(def stop-system dev/stop)
(def start-system dev/start)