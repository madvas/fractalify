(ns fractalify.fractals.components.canvas-controls
  (:require-macros [clairvoyant.core :refer [trace-forms]]
                   [fractalify.tracer-macros :refer [trace-views]])
  (:require [re-frame.core :as f]
            [reagent.core :as r]
            [fractalify.styles :as y]
            [fractalify.components.form-text :as form-text]
            [fractalify.fractals.components.rule-input :as rule-input]
            [fractalify.utils :as u]
            [fractalify.components.text-field :as text-field]))

(declare canvas-controls)

(trace-views
  (defn canvas-controls []
    (let [values (f/subscribe [:form-data :l-system])]
      (fn []
        [:div.row.pad-20
         [:div.col-xs-1
          [form-text/form-text [:l-system :angle]
           {:floatingLabelText "Angle"
            :type "number"}]]
         [:div.col-xs-1
          [form-text/form-text [:l-system :iterations]
           {:floatingLabelText "Iternations"
            :type "number"}]]
         [:div.col-xs-1
          [form-text/form-text [:l-system :line-length]
           {:floatingLabelText "Length"
            :type "number"}]]
         [:div.col-xs-2
          [form-text/form-text [:l-system :start]
           {:floatingLabelText "Start"}]]
         (for [i (range (count (:rules @values)))]
           ^{:key i}
           [:div.col-xs-12
            [rule-input/rule-input i]]
           )
         [form-text/form-text [:l-system :result-cmds]
          {:floatingLabelText "Result"
           :disabled          true}]]))))
