(ns fractalify.system
  (:require [com.stuartsierra.component :as component]
            [plumbing.core :as p]))

(defn start
  "Performs side effects to initialize the system, acquire resources,
  and start it running. Returns an updated instance of the system."
  [system]
  )

(defn stop
  "Performs side effects to shut down the system and release its
  resources. Returns an updated instance of the system."
  [system]
  )

(defn example-system [config-options]
  (p/letk [[web-server-config] config-options]
    (-> (component/system-map
          :config-options config-options
          :web-server web-server-config)
        )))

(defn -main []
  )
