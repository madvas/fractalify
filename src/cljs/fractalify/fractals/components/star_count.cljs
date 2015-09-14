(ns fractalify.fractals.components.star-count
  (:require [material-ui.core :as ui]))

(defn star-count [_]
  (fn [count props]
    [:div props
     [ui/font-icon
      {:style      {:font-size "1.2em"}
       :class-name "mdi mdi-star"}]
     [:span count]]))