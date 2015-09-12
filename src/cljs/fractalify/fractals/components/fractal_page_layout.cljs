(ns fractalify.fractals.components.fractal-page-layout
  (:require [fractalify.styles :as y]
            [material-ui.core :as ui]))

(defn fractal-page-layout [canvas-section btns-section sidebar-section]
  [:div.row
   [:div.col-xs-12.col-sm-7.col-md-6.col-lg-5.col-lg-offset-1.relative
    [ui/paper {:style y/canvas-paper-wrap}
     canvas-section]
    btns-section]
   [:div.col-xs-12.col-sm-5.col-md-offset-1.col-md-5.col-lg-4.col-lg-offset-2
    sidebar-section]])