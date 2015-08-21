(ns fractalify.core
  (:require [reagent.core :as reagent]
            [re-frame.core :as re-frame]
            [fractalify.main.handlers]
            [fractalify.main.subs]
            [fractalify.main.routes]
            [fractalify.main.views]
            [fractalify.auth.handlers]
            [fractalify.auth.subs]
            [fractalify.auth.routes]
            [fractalify.auth.views]))

(enable-console-print!)

(defn mount-root []
  (reagent/render [fractalify.main.views/main-panel]
                  (.getElementById js/document "app")))

(fractalify.auth.routes/app-routes)
(fractalify.main.routes/app-routes)
(re-frame/dispatch-sync [:initialize-db])

(defn ^:export init []
  (mount-root))
