(ns fractalify.fractals.components.fractal-create
  (:require [re-frame.core :as f]
            [reagent.core :as r]
            [material-ui.core :as ui]
            [fractalify.fractals.components.canvas :as canvas]
            [fractalify.styles :as y]
            [fractalify.fractals.components.canvas-controls :as canvas-controls]
            [fractalify.utils :as u]))

(defn fractal-create []
  (let [l-system-generating? (f/subscribe [:l-system-generating])]
    (fn []
      [:div.row
       [:div.col-xs-12.col-sm-7.col-md-offset-0.col-md-6.col-lg-5.col-lg-offset-1.relative
        [ui/paper {:className "canvas-paper-wrap"}
         [ui/refresh-indicator (merge
                                 {:status "loading"}
                                 (select-keys y/canvas-indicator [:left :top])
                                 {:style (merge
                                           (dissoc y/canvas-indicator :left :top)
                                           {:opacity (if @l-system-generating? 1 0)})})]
         [canvas/canvas]]]
       [:div.col-xs-12.col-sm-5.col-sm-offset-0.col-md-offset-1.col-md-5.col-lg-4.col-lg-offset-2
          [ui/paper
           [canvas-controls/canvas-controls]]]])))
