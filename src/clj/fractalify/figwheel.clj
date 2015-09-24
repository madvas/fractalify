(ns fractalify.figwheel
  (:require
    [com.stuartsierra.component :as c]
    [figwheel-sidecar.core :as fig]
    [figwheel-sidecar.auto-builder :as fig-auto]
    [plumbing.core :as p]
    [clojurescript-build.auto :as auto]
    [fractalify.utils :as u]))


(def build-cfg {:builds [{:id           "dev"
                          :source-paths ["src/cljs" "src/cljc" "env/dev/cljs"]
                          :compiler     {:output-to            "resources/public/js/app.js"
                                         :output-dir           "resources/public/js/out"
                                         :source-map           true
                                         :optimizations        :none
                                         :source-map-timestamp true
                                         :preamble             ["react/react.min.js"]}}]})

(def fig-cfg {:css-dirs    ["resources/public/css"]
              :server-port 3449})

(defrecord Figwheel []
  c/Lifecycle
  (start [this]
    (-> this
        (assoc :figwheel (fig/start-server fig-cfg))
        (#(assoc % :builder (fig-auto/autobuild*
                              (assoc build-cfg :figwheel-server (:figwheel %)))))))

  (stop [this]
    (p/when-letk [[figwheel builder] this]
      (fig/stop-server figwheel)
      (auto/stop-autobuild! builder))
    (dissoc this :figwheel :builder)))

(def new-figwheel map->Figwheel)

