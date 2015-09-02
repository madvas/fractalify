(ns fractalify.fractals.components.canvas-controls
  (:require [re-frame.core :as f]
            [fractalify.fractals.components.rule-input :as rule-input]
            [fractalify.fractals.components.cmd-input :as cmd-input]
            [fractalify.utils :as u]
            [fractalify.fractals.components.control-text :as control-text]))

(defn rules []
  (let [rules (f/subscribe [:rules])]
    (fn []
      [:div.row.col-xs-12
       (for [i (range 1 (count @rules))]
         ^{:key i}
         [:div.col-xs-12
          [rule-input/rule-input i]])])))

(defn commands []
  (let [cmd-map (f/subscribe [:get-form-item :l-system :cmd-map])]
    (fn []
      [:div.row.col-xs-12
       (for [cmd-key (keys @cmd-map)]
         ^{:key cmd-key}
         [:div.col-xs-12
          [cmd-input/cmd-input cmd-key (@cmd-map cmd-key)]])])))

(defn canvas-controls []
  [:div.row.pad-20
   [:div.col-xs-6.col-sm-6.col-md-4
    [control-text/control-text [:l-system :origin :x]
     {:floating-label-text "Start X"
      :type                "number"}]]
   [:div.col-xs-6.col-sm-6.col-md-4
    [control-text/control-text [:l-system :origin :y]
     {:floating-label-text "Start Y"
      :type                "number"}]]
   [:div.col-xs-6.col-sm-6.col-md-4
    [control-text/control-text [:l-system :start-angle]
     {:floating-label-text "Start Angle"
      :type                "number"}]]
   [:div.col-xs-6.col-sm-6.col-md-4
    [control-text/control-text [:l-system :iterations]
     {:floating-label-text "Iterations"
      :type                "number"}]]
   [:div.col-xs-6.col-sm-6.col-md-4
    [control-text/control-text [:l-system :angle]
     {:floating-label-text "Angle"
      :type                "number"}]]
   [:div.col-xs-6.col-sm-6.col-md-4
    [control-text/control-text [:l-system :line-length]
     {:floating-label-text "Line Length"
      :type                "number"}]]
   [:div.col-xs-12
    [:h4 "Commands"]
    [commands]]
   [:div.col-xs-3
    [control-text/control-text [:l-system :start]
     {:floating-label-text "Start"}]]
   [rules]])
