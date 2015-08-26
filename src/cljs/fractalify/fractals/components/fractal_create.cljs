(ns fractalify.fractals.components.fractal-create
  (:require [re-frame.core :as f]
            [material-ui.core :as ui]
            [fractalify.fractals.components.canvas :as canvas]
            [fractalify.fractals.components.canvas-controls :as canvas-controls]
            [fractalify.styles :as y]))

(defn fractal-create []
  (let [params (f/subscribe [:route-params])]
    (fn []
      [:div.row.center-xs
       [:div.col-xs-12.col-sm-8.col-md-6.col-lg-4.mar-top-20
        [ui/paper {:style y/canvas-size}
         [canvas/canvas]]]
       [:div.col-xs-12.mar-top-20
        [ui/paper
         [canvas-controls/canvas-controls]]]])))
