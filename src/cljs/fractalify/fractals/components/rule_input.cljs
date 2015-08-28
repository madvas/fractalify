(ns fractalify.fractals.components.rule-input
  (:require [re-frame.core :as f]
            [fractalify.components.form-text :as form-text]
            [fractalify.fractals.components.control-text :as control-text]))



(defn rule-input [rule-index]
  (fn []
    [:div.row
     [:div.col-xs-1
      [control-text/control-text [:l-system :rules rule-index 0]
       {:floatingLabelText "Var"}]]
     [:div.col-xs-4
      [control-text/control-text [:l-system :rules rule-index 1]
       {:floatingLabelText "Rule"}]]]))
