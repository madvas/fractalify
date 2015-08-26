(ns fractalify.fractals.handlers
  (:require [fractalify.middleware :as m]
            [re-frame.core :as r]
            [fractalify.main.handlers :as h]
            [re-frame.core :as f]
            [fractalify.router :as t]
            [fractalify.fractals.lib.l-systems :as l]
            [fractalify.fractals.lib.turtle :as turtle]))

(r/register-handler
  :render-l-system
  m/standard-middlewares
  (fn [db [canvas-dom l-system]]
    (let [commands (l/l-system l-system)
          new-db (assoc-in db [:forms :l-system :result-cmds] commands)]
      (turtle/render! canvas-dom (get-in new-db [:forms :l-system]))
      new-db)))






