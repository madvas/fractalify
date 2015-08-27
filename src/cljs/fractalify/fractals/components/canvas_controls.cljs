(ns fractalify.fractals.components.canvas-controls
  (:require [re-frame.core :as f]
            [fractalify.components.form-text :as form-text]
            [fractalify.fractals.components.rule-input :as rule-input]
            [fractalify.utils :as u]))

(defn rules []
  (let [rules (f/subscribe [:rules])]
    (fn []
      [:div.row.col-xs-12
       (for [i (range 1 (count @rules))]
         ^{:key i}
         [:div.col-xs-12
          [rule-input/rule-input i]]
         )])))

(defn canvas-controls []
  [:div.row.pad-20
   [:div.col-xs-1
    [form-text/form-text [:l-system :angle]
     {:floatingLabelText "Angle"
      :type              "number"}]]
   [:div.col-xs-1
    [form-text/form-text [:l-system :iterations]
     {:floatingLabelText "Iternations"
      :type              "number"}]]
   [:div.col-xs-1
    [form-text/form-text [:l-system :line-length]
     {:floatingLabelText "Length"
      :type              "number"}]]
   [:div.col-xs-1
    [form-text/form-text [:l-system :origin :x]
     {:floatingLabelText "X"
      :type              "number"}]]
   [:div.col-xs-1
    [form-text/form-text [:l-system :origin :y]
     {:floatingLabelText "Y"
      :type              "number"}]]
   [:div.col-xs-2
    [form-text/form-text [:l-system :start-angle]
     {:floatingLabelText "Start Angle"
      :type              "number"}]]
   [:div.col-xs-2
    [form-text/form-text [:l-system :start]
     {:floatingLabelText "Start"}]]
   [rules]])
