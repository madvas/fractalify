(ns fractalify.main.routes
  (:require [re-frame.core :as r]
            [fractalify.router :as t]
            [fractalify.main.components.home :as home]
            [fractalify.main.components.about :as about]
            [fractalify.main.components.contact :as contact]))


(defn define-routes! []

  (t/add-routes! {""        :home
                  "about"   :about
                  "contact" :contact})

  (defmethod t/panels :home [] [home/home])
  (defmethod t/panels :about [] [about/about])
  (defmethod t/panels :contact [] [contact/contact])
  (defmethod t/panels :default [] [:h1 "Page not found"]))


