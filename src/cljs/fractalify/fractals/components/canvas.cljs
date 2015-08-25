(ns fractalify.fractals.components.canvas
  (:require-macros [clairvoyant.core :refer [trace-forms]]
                   [fractalify.tracer-macros :refer [trace-views]])
  (:require [re-frame.core :as f]
            [reagent.core :as r]
            [fractalify.styles :as y]
            [fractalify.utils :as u]))

(trace-views
  (defn canvas []
    (let [params (f/subscribe [:route-params])]
      (r/create-class
        {
         :component-will-update (fn [next-props next-state]
                                  (println ":component-will-update"))
         :reagent-render
                                (fn []
                                  [:canvas {:style y/canvas}])}))))
