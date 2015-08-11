(ns fractalify.core
    (:require [reagent.core :as reagent]
              [re-frame.core :as re-frame]
              [fractalify.handlers]
              [fractalify.subs]
              [fractalify.routes :as routes]
              [fractalify.views :as views]))

(defn mount-root []
  (reagent/render [views/main-panel]
                  (.getElementById js/document "app")))

(defn ^:export init [] 
  (routes/app-routes)
  (re-frame/dispatch-sync [:initialize-db])
  (mount-root))
