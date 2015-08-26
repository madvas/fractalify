(ns fractalify.fractals.components.canvas
  (:require-macros [fractalify.tracer-macros :refer [trace-views]])
  (:require [re-frame.core :as f]
            [reagent.core :as r]
            [fractalify.styles :as y]
            [fractalify.utils :as u]
            [fractalify.fractals.lib.turtle :as turtle]))

(declare canvas)

(defn dispatch-render [this l-system]
  (f/dispatch [:render-l-system (r/dom-node this) l-system]))

(trace-views
  (defn canvas []
    (let [l-system (f/subscribe [:new-l-system])]
      (r/create-class
        {:component-did-mount
         (fn [this]
           (dispatch-render this @l-system))
         :component-will-update
         (fn [this]
           (dispatch-render this @l-system))
         :reagent-render
         (fn []
           @l-system
           [:canvas y/canvas-size])}))))
