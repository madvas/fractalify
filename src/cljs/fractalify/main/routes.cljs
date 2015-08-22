(ns fractalify.main.routes
  (:require-macros [secretary.core :refer [defroute]])
  (:import goog.History)
  (:require [secretary.core :as secretary]
            [re-frame.core :as r]
            [fractalify.router :as t]
            [fractalify.main.components.home :as home]
            [fractalify.main.components.about :as about]))


(defn app-routes []
  (secretary/set-config! :prefix "#")
  (t/add-route! :home (defroute "/" []
                                (r/dispatch [:set-active-panel :home])))

  (defmethod t/panels :home [] [home/home])

  (t/add-route! :about (defroute "/about" []
                                 (r/dispatch [:set-active-panel :about])))
  (defmethod t/panels :about [] [about/about])

  (defmethod t/panels :default [] [:p "Page not found"]))


