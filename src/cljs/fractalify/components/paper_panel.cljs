(ns fractalify.components.paper-panel
  (:require [fractalify.components.responsive-panel :as responsive-panel]
            [fractalify.components.paper-content :as paper-content]))

(defn paper-panel [& children]
  [responsive-panel/responsive-panel
   (into [] (concat [paper-content/paper-content] children))])
