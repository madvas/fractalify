(ns fractalify.figwheel
  (:require
    [com.stuartsierra.component :as c]
    [figwheel-sidecar.core :as fig]
    [figwheel-sidecar.auto-builder :as fig-auto]))


(defrecord Figwheel []
  c/Lifecycle
  (start [this]
    (let [figwheel (fig/start-server {:css-dirs ["resources/public/css"]})
          config {:builds          [{:id           "dev"
                                     :source-paths ["src/cljs" "src/cljc" "env/dev/cljs"]
                                     :compiler     {:output-to            "resources/public/js/app.js"
                                                    :output-dir           "resources/public/js/out"
                                                    :source-map           true
                                                    :optimizations        :none
                                                    :source-map-timestamp true
                                                    :preamble             ["react/react.min.js"]}}]
                  :figwheel-server figwheel}]
      (fig-auto/autobuild* config)
      (assoc this :figwheel figwheel)))

  (stop [this]
    (when-let [fig (:figwheel this)]
      (fig/stop-server fig))
    (dissoc this :figwheel)))

(def new-figwheel ->Figwheel)

