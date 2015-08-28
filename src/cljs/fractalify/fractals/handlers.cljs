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
            [fractalify.fractals.lib.workers.simple :as simple]
    ;[fractalify.fractals.lib.parallel :as parallel]
            [cljs.core.async :refer [<!]]))


(def worker-count 1)
(def worker-script "/js/simple.js")
;(def servant-channel (servant/spawn-servants worker-count worker-script))

(r/register-handler
  :render-l-system
  m/standard-middlewares
  (fn [db [canvas-dom l-system]]
    (let [result-cmds (l/l-system l-system)
          ;lines (turtle/gen-lines-coords l-system result-cmds)
          servant-ch (servant/spawn-servants worker-count worker-script)
          lines-ch (servant/servant-thread
                     servant-ch
                     servant/standard-message
                     "simple")
          ]
      (println (servant/f->key fractalify.fractals.lib.workers.simple/simple))
      (println lines-ch)

      (go
          (println (<! lines-ch))
          (println "here"))
      ;(renderer/render! canvas-dom lines)
      db)))






