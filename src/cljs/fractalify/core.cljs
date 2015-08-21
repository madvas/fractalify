(ns fractalify.core
  (:require [reagent.core :as reagent]
            [re-frame.core :as re-frame]
            [fractalify.history :as history]
            [fractalify.main.handlers]
            [fractalify.main.subs]
            [fractalify.main.routes]
            [fractalify.main.view]
            [fractalify.users.handlers]
            [fractalify.users.subs]
            [fractalify.users.routes]
            [schema.core :as s]))

(enable-console-print!)
(s/set-fn-validation! goog.DEBUG)

(defn mount-root []
  (reagent/render [fractalify.main.view/main-view]
                  (.getElementById js/document "app")))

(fractalify.users.routes/app-routes)
(fractalify.main.routes/app-routes)
(history/setup!)
(re-frame/dispatch-sync [:initialize-db])

(defn ^:export init []
  (mount-root))
