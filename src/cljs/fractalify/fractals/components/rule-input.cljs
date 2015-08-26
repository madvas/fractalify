(ns fractalify.fractals.components.rule-input
  (:require [re-frame.core :as f]
            [fractalify.components.form-text :as form-text]))

(defn rule-input [rule-index]
  (fn []
    [:div.row
     [:div.col-xs-1
      [form-text/form-text [:l-system :rules rule-index 0]
       {:floatingLabelText "Var"}]]
     [:div.col-xs-4
      [form-text/form-text [:l-system :rules rule-index 1]
       {:floatingLabelText "Rule"}]]]))
