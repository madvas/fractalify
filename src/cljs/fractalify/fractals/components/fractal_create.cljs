(ns fractalify.fractals.components.fractal-create
  (:require [re-frame.core :as f]
            [material-ui.core :as ui]
            [fractalify.fractals.components.canvas :as canvas]
            [fractalify.styles :as y]
            [fractalify.fractals.components.canvas-controls :as canvas-controls]))

(defn fractal-create []
  (let [l-system-generating? (f/subscribe [:l-system-generating])]
    (fn []
      [:div.row.center-xs
       [:div.col-xs-12.col-sm-8.col-md-6.col-lg-4.mar-top-20.relative
        [ui/paper {:style y/canvas-size}
         [ui/refresh-indicator (merge
                                 {:status "loading"}
                                 (select-keys y/canvas-indicator [:left :top])
                                 {:style (merge
                                           (dissoc y/canvas-indicator :left :top)
                                           {:opacity (if @l-system-generating? 1 0)})})]
         [canvas/canvas]]]
       [:div.col-xs-12.mar-top-20
        [ui/paper
         [canvas-controls/canvas-controls]]]])))
