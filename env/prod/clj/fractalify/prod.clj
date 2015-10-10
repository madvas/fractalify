(ns fractalify.prod
  (:require [fractalify.system :as sys]
            [com.stuartsierra.component :as c])
  (:gen-class))

(defn -main [& args]
  (println "Starting system...")
  (let [system (sys/new-production-system)]
    (c/start system)
    (println "System started")))

