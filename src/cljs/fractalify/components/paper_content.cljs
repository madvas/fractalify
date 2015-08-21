(ns fractalify.components.paper-content
  (:require [fractalify.styles :as y]
            [material-ui.core :as ui]))

(defn paper-content [& children]
  [ui/paper
   {:style y/pad-20}
   (into [:div.row] children)])