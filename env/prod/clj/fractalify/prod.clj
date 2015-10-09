(ns fractalify.prod
  (:gen-class))

(defn -main [& args]

  (eval '(do
           (require '[fractalify.system :as sys])
           (require '[com.stuartsierra.component :as c])
           (println "Starting system...")
           (let [system (sys/new-production-system)]
             (c/start system)
             (println "System started"))
           )))

