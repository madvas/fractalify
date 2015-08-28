(ns fractalify.fractals.handlers
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [fractalify.middleware :as m]
            [re-frame.core :as r]
            [fractalify.main.handlers :as h]
            [re-frame.core :as f]
            [fractalify.router :as t]
            [fractalify.fractals.lib.l-systems :as l]
            [fractalify.fractals.lib.workers.turtle :as turtle]
            [fractalify.fractals.lib.renderer :as renderer]
            [servant.core :as servant]
            [servant.worker :as worker]
            [cljs.core.async :refer [<!]]))


(def worker-count 1)
(def worker-script "/js/workers.js")
;(def servant-channel (servant/spawn-servants worker-count worker-script))

(r/register-handler
  :render-l-system
  m/standard-middlewares
  (fn [db [canvas-dom l-system]]
    (let [result-cmds (l/l-system l-system)
          ;lines (turtle/gen-lines-coords l-system result-cmds)
          servant-ch (servant/spawn-servants worker-count worker-script)
          #_ lines-ch #_ (servant/servant-thread
                     servant-ch
                     servant/standard-message
                     turtle/gen-lines-coords-worker l-system result-cmds)
          ]
      #_ (println lines-ch)

      #_ (go
        (println (<! lines-ch))
        (println "here"))
      ;(renderer/render! canvas-dom lines)
      db)))






