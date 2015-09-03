(ns fractalify.fractals.components.canvas-controls
  (:require [re-frame.core :as f]
            [fractalify.fractals.components.rule-input :as rule-input]
            [fractalify.fractals.components.cmd-input :as cmd-input]
            [fractalify.utils :as u]
            [fractalify.fractals.components.control-text :as control-text]
            [material-ui.core :as ui]
            [clojure.string :as str]))

(defn rules []
  (let [rules (f/subscribe [:get-form-item :l-system :rules])]
    (fn []
      [:div.row.col-xs-12
       (for [k (keys @rules)]
         ^{:key k}
         [:div.col-xs-12.col-md-6
          [rule-input/rule-input k]])])))

(defn commands []
  (let [cmds (f/subscribe [:get-form-item :l-system :cmds])]
    (fn []
      [:div.row.col-xs-12
       (for [k (keys @cmds)]
         ^{:key k}
         [:div.col-xs-12
          [cmd-input/cmd-input k]])])))

(defn canvas-controls []
  [:div.row.mar-0
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
     {:floating-label-text "Rotation Angle"
      :type                "number"}]]
   [:div.col-xs-6.col-sm-6.col-md-4
    [control-text/control-text [:l-system :line-length]
     {:floating-label-text "Line Length"
      :type                "number"}]]
   [:div.col-xs-3
    [control-text/control-text [:l-system :start]
     {:floating-label-text "Start"}]]
   [ui/tabs
    [ui/tab {:label "Actions"}
     [commands]]
    [ui/tab {:label "Rules"}
     [rules]]]])
