(ns fractalify.fractals.components.canvas
  (:require [re-frame.core :as f]
            [reagent.core :as r]
            [fractalify.styles :as y]
            [fractalify.utils :as u]))

(defn dispatch [this type & args]
  (println "dispatchng: " type)
  (f/dispatch (u/concat-vec [type (r/dom-node this)] args)))

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

(defn l-system []
  (let [l-system (f/subscribe [:l-system-new])]
    (r/create-class
      {:component-did-mount
       (fn []
         (f/dispatch [:l-system-change @l-system]))
       :component-will-update
       (fn []
         (f/dispatch [:l-system-change @l-system]))
       :reagent-render
       (fn []
         @l-system
         [:div])})))

(defn canvas []
  [:div
   [canvas-el]
   [l-system]])



