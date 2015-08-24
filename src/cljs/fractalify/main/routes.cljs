(ns fractalify.main.routes
  (:require [re-frame.core :as r]
            [fractalify.router :as t]
            [fractalify.main.components.home :as home]
            [fractalify.main.components.about :as about]))


(defn define-routes! []

  (t/add-routes! {""      :home
                  "about" :about})

  (defmethod t/panels :home [] [home/home])
  (defmethod t/panels :about [] [about/about])
  (defmethod t/panels :default [] [:h1 "Page not found"]))


