(ns fractalify.fractals.components.l-system-operations.rule
  (:require [re-frame.core :as f]
            [fractalify.fractals.components.control-text :as control-text]
            [material-ui.core :as ui]
            [fractalify.utils :as u]
            [fractalify.fractals.components.l-system-operations.remove-btn :as remove-btn]
            [schema.core :as s :include-macros true]
            [workers.turtle.schemas :as ch]))

(s/defn rule
  [k :- s/Int
   rule-item :- ch/Rule]
  (let [[var-name rule-val] rule-item]
    (fn []
      [:div.row
       [:div.col-xs-3
        [control-text/control-text var-name "Variable" [:l-system :rules k 0]]]
       [:div.col-xs-8
        [control-text/control-text rule-val "Rule" [:l-system :rules k 1]]]
       [remove-btn/remove-btn :rules k]])))
