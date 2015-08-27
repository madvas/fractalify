(ns fractalify.fractals.handlers
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [fractalify.middleware :as m]
            [re-frame.core :as r]
            [fractalify.main.handlers :as h]
            [re-frame.core :as f]
            [fractalify.router :as t]
            [fractalify.fractals.lib.l-systems :as l]
            [fractalify.fractals.lib.turtle :as turtle]
            [fractalify.fractals.lib.renderer :as renderer]
            [servant.core :as servant]
            [servant.worker :as worker]
            [cljs.core.async :refer [<!]]))


(def worker-count 1)
(def worker-script "/js/app.js")
;(def servant-channel (servant/spawn-servants worker-count worker-script))

(r/register-handler
  :render-l-system
  m/standard-middlewares
  (fn [db [canvas-dom l-system]]
    (let [result-cmds (l/l-system l-system)
          ;lines (turtle/gen-lines-coords l-system result-cmds)
          ;lines-chan (servant/spawn-servants worker-count worker-script)
          ]
      #_ (let [lines-chan (servant/servant-thread
                         lines-chan
                         servant/standard-message
                         turtle/gen-lines-coords-worker l-system result-cmds)])

      #_ (go
        (println (<! lines-chan)))
      ;(renderer/render! canvas-dom lines)
      db)))






