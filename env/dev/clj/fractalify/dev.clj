(ns fractalify.dev
  (:require [environ.core :refer [env]]
            [net.cgrand.enlive-html :refer [set-attr prepend append html]]
            [cemerick.piggieback :as piggieback]
            [weasel.repl.websocket :as weasel]
            [clojure.pprint :refer (pprint)]
            [clojure.reflect :refer (reflect)]
            [clojure.repl :refer (apropos dir doc find-doc pst source)]
            [clojure.tools.namespace.repl :as tools-repl]
            [com.stuartsierra.component :as c]
            [fractalify.system :as sys]
            [clojure.java.io :as io]
            [fractalify.utils :as u]
            [fractalify.config :as cnf]
            [modular.bidi :as mb]
            [schema.core :as s]
            [com.rpl.specter :as e]))

(defn browser-repl []
  (let [repl-env (weasel/repl-env :ip "0.0.0.0" :port 9001)]
    (piggieback/cljs-repl :repl-env repl-env)))

(def system nil)
(s/set-fn-validation! true)

(defn new-dev-system
  "Create a development system"
  []
  (let [config (cnf/config)
        s-map (-> (sys/new-system-map config)
                  (sys/dev-system-map config))]
    (-> s-map
        (c/system-using
          (merge (sys/new-dependency-map)
                 (sys/dev-dependency-map)))
        )))

(defn init [init-system]
  "Constructs the current development system."
  []
  (alter-var-root #'system (constantly init-system)))

(defn start
  "Starts the current development system."
  []
  (alter-var-root
    #'system
    c/start
    ))

(defn stop
  "Shuts down and destroys the current development system."
  []
  (alter-var-root #'system
                  (fn [s] (when s (c/stop s)))))

(defn go
  "Initializes the current development system and starts it running."
  []
  (println "Go called")
  (init (new-dev-system))
  (start)
  :ok)

(defn repl-refresh []
  (tools-repl/refresh :after 'fractalify.dev/go))

(defn reset []
  (stop)
  (repl-refresh))

;; REPL Convenience helpers

(defn routes []
  (-> system :router :router))

(defn match-route [path]
  (bidi.bidi/match-route (routes) path))

(defn path-for [path & args]
  (apply mb/path-for
         (-> system :router) path args))

