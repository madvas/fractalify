(ns fractalify.dev
  (:require [environ.core :refer [env]]
            [net.cgrand.enlive-html :refer [set-attr prepend append html]]
            [cemerick.piggieback :as piggieback]
            [weasel.repl.websocket :as weasel]
            [figwheel-sidecar.auto-builder :as fig-auto]
            [figwheel-sidecar.core :as fig]
            [clojurescript-build.auto :as auto]
            [clojure.java.shell :refer [sh]]
            [com.stuartsierra.component :as component]
            [clojure.tools.namespace.repl :refer (refresh)]
            [fractalify.system :as system]))

(def is-dev? (env :is-dev))

(def ^:dynamic *fig-server* (atom nil))

(def inject-devmode-html
  (comp
    (set-attr :class "is-dev")
    (prepend (html [:script {:type "text/javascript" :src "/js/out/goog/base.js"}]))
    (append (html [:script {:type "text/javascript"} "goog.require('fractalify.main')"]))))

(defn browser-repl []
  (let [repl-env (weasel/repl-env :ip "0.0.0.0" :port 9001)]
    (piggieback/cljs-repl :repl-env repl-env)))

(defn start-figwheel []
  (let [server (fig/start-server {:css-dirs ["resources/public/css"]})
        config {:builds          [{:id           "dev"
                                   :source-paths ["src/cljs" "env/dev/cljs"]
                                   :compiler     {:output-to            "resources/public/js/app.js"
                                                  :output-dir           "resources/public/js/out"
                                                  :source-map           true
                                                  :optimizations        :none
                                                  :source-map-timestamp true
                                                  :preamble             ["react/react.min.js"]}}]
                :figwheel-server server}]
    (fig-auto/autobuild* config)
    (reset! *fig-server* server)))

(defn stop-figwheel []
  (fig/stop-server @*fig-server*))

(defn start-less []
  (future
    (println "Starting less.")
    (sh "lein" "less" "auto")))


(def system nil)

(defn init []
  (alter-var-root #'system
                  (constantly (system/start {:host "dbhost.com" :port 123}))))

(defn start []
  (alter-var-root #'system component/start))

(defn stop []
  (alter-var-root #'system
                  (fn [s] (when s (component/stop s)))))

(defn go []
  (init)
  (start))

(defn reset []
  (stop)
  (refresh :after 'dev.user/go))