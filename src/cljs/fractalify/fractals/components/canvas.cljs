(ns fractalify.fractals.components.canvas
  (:require [re-frame.core :as f]
            [reagent.core :as r]
            [fractalify.styles :as y]))

(defn canvas-el []
  (let [canvas (f/subscribe [:canvas])]
    (r/create-class
      {:component-will-update
       (fn [this]
         (f/dispatch [:canvas-change (r/dom-node this) @canvas]))
       :reagent-render
       (fn []
         [:canvas
          {:width  (:size @canvas)
           :height (:size @canvas)
           :style  y/canvas-style}])})))

(defn- dispatch-l-system [this l-system-new]
  (let [l-system-old (:l-system (r/state this))]
    (f/dispatch [:l-system-change l-system-new l-system-old])
    (r/set-state this {:l-system l-system-new})))

(defn l-system []
  (let [l-system (f/subscribe [:l-system-new])]
    (r/create-class
      {:component-did-mount
       (fn [this]
         (dispatch-l-system this @l-system))
       :component-will-update
       (fn [this]
         (dispatch-l-system this @l-system))
       :reagent-render
       (fn []
         @l-system
         [:div])})))

(defn canvas []
  [:div
   [canvas-el]
   [l-system]])



