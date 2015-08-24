(ns fractalify.fractals.components.fractal-create
  (:require [re-frame.core :as f]))

(defn fractal-create []
  (let [params (f/subscribe [:route-params])]
    (fn []
      [:div {} @params])))
