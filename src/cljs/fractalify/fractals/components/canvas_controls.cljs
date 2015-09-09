(ns fractalify.fractals.components.canvas-controls
  (:require [re-frame.core :as f]
            [fractalify.utils :as u]
            [fractalify.fractals.components.control-text :as control-text]
            [material-ui.core :as ui]
            [fractalify.styles :as y]
            [fractalify.fractals.components.l-system-operations.tab :as tab]
            [fractalify.components.color-picker :as color-picker]))


(defn canvas-controls []
  [:div.col-xs-12.col-sm-5.col-md-offset-1.col-md-5.col-lg-4.col-lg-offset-2
   [ui/paper
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
      [control-text/control-text [:canvas :size]
       {:floating-label-text "Canvas Size"
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
     [:div.col-xs-6.col-sm-6.col-md-4
      [control-text/control-text [:canvas :line-width]
       {:floating-label-text "Line Width"
        :type                "number"}]]
     [:div.col-xs-6.col-sm-6.col-md-4
      [control-text/control-text [:l-system :start]
       {:floating-label-text "Start"}]]
     [:div.row.col-xs-6.center-xs.middle-xs.start-md
      [:div.col-xs-4.col-md-2
       [color-picker/color-picker
        [:form-item :fractals :canvas :color]
        {:debounce      300
         :trigger-props {:icon-class-name "mdi mdi-brush"
                         :tooltip         "Choose Line Color"}}]]
      [:div.col-xs-4.col-md-2
       [color-picker/color-picker
        [:form-item :fractals :canvas :bg-color]
        {:debounce      300
         :trigger-props {:tooltip         "Choose Background Color"
                         :icon-class-name "mdi mdi-format-paint"}}]]]
     [:div.col-xs-12.pad-0
      [ui/tabs {:style y/pad-bot-15}
       ^{:key 1} (tab/tab :cmds {:label "Actions"})
       ^{:key 2} (tab/tab :rules {:label "Rules"})]]]]])
