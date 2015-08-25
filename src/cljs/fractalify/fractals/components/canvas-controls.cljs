(ns fractalify.fractals.components.canvas-controls
  (:require-macros [clairvoyant.core :refer [trace-forms]]
                   [fractalify.tracer-macros :refer [trace-views]])
  (:require [re-frame.core :as f]
            [reagent.core :as r]
            [fractalify.styles :as y]
            [fractalify.components.form-text :as form-text]))


(trace-views
  (defn canvas-controls []
    (let [values (f/subscribe [:form-data :fractal])]
      (fn []
        (println @values)
        [:div.row {:style {:height 400}}
         [form-text/form-text [:fractal :rules]
          {:floatingLabelText "Rule"}]]))))
