(ns fractalify.fractals.components.fractal-hints
  (:require [material-ui.core :as ui]))


(defn what-is-this-hint []
  [:div.col-xs-12.middle-xs.start-xs
   [:a.row.middle-xs.pad-lef-10.no-dec
    {:href   "https://en.wikipedia.org/wiki/L-system"
     :target "_blank"}
    [:h5.mar-rig-5 "What is this?"]
    [ui/font-icon
     {:class-name "mdi mdi-help-circle"
      :style      {:font-size "1.2em"}
      :color      (ui/color :grey600)}]]])

(defn chars-hint []
  [:h5.col-xs-12.mar-top-10
   "Note: Be aware of character differences like minus vs. hyphen"])