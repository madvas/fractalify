(ns fractalify.core
    (:require [reagent.core :as reagent]
              [re-frame.core :as re-frame]
              [fractalify.main.handlers]
              [fractalify.main.subs]
              [fractalify.main.routes :as routes]
              [fractalify.main.views :as views]))

;(enable-console-print!)

(defn mount-root []
  (reagent/render [views/main-panel]
                  (.getElementById js/document "app")))

(defn ^:export init [] 
  (routes/app-routes)
  (re-frame/dispatch-sync [:initialize-db])
  (mount-root))
