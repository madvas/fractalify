(ns fractalify.components.content
  (:require [reagent.core :as r]
            [re-frame.core :as f]))

(defn content []
  (let [active-panel (f/subscribe [:active-panel])]
    (r/create-class
      {:component-will-mount (fn []
                               (println "will mount")
                               true)
       :reagent-render
                             (fn [& children]
                               (into [:div] children))})))
