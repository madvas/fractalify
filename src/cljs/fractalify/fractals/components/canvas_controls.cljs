(ns fractalify.fractals.components.canvas-controls
  (:require [re-frame.core :as f]
            [fractalify.utils :as u]
            [fractalify.fractals.components.control-text :as control-text]
            [material-ui.core :as ui]
            [fractalify.styles :as y]
            [fractalify.fractals.components.l-system-operations.tab :as tab]
            [fractalify.components.color-picker :as color-picker]
            [schema.core :as s :include-macros true]
            [fractalify.main.schemas :as ch]
            [plumbing.core :as p]))


(s/defn canvas-controls [fractal-sub]
  (let [fractal (f/subscribe fractal-sub)]
    (fn []
      (p/letk [[l-system canvas] @fractal
               [[:origin x y] start start-angle iterations angle line-length] l-system
               [size line-width color bg-color] canvas]
        [ui/paper
         [:div.row.mar-0
          [:div.col-xs-6.col-sm-6.col-md-4
           [control-text/control-number x "Start X" [:l-system :origin :x]]]
          [:div.col-xs-6.col-sm-6.col-md-4
           [control-text/control-number y "Start Y" [:l-system :origin :y]]]
          [:div.col-xs-6.col-sm-6.col-md-4
           [control-text/control-number size "Canvas Size" [:canvas :size]]]
          [:div.col-xs-6.col-sm-6.col-md-4
           [control-text/control-number start-angle "Start Angle" [:l-system :start-angle]]]
          [:div.col-xs-6.col-sm-6.col-md-4
           [control-text/control-number iterations "Iterations" [:l-system :iterations]]]
          [:div.col-xs-6.col-sm-6.col-md-4
           [control-text/control-number angle "Rotation Angle" [:l-system :angle]]]
          [:div.col-xs-6.col-sm-6.col-md-4
           [control-text/control-number line-length "Line Length" [:l-system :line-length]]]
          [:div.col-xs-6.col-sm-6.col-md-4
           [control-text/control-number line-width "Line Width" [:canvas :line-width]]]
          [:div.col-xs-6.col-sm-6.col-md-4
           [control-text/control-text start "Start" [:l-system :start]]]

          [:div.row.col-xs-6.center-xs.middle-xs.start-md
           (for [[k clr tooltip icon] [[1 color "Choose Line Color" "brush"]
                                       [2 bg-color "Choose Background Color" "format-paint"]]]
             ^{:key k}
             [:div.col-xs-4.col-md-2
              [color-picker/color-picker clr
               [:form-item :fractals :canvas :color]
               {:debounce      300
                :trigger-props {:tooltip         tooltip
                                :icon-class-name (str "mdi mdi-" icon)}}]])]
          [:div.col-xs-12.pad-0
           [ui/tabs {:style y/pad-bot-15}
            ^{:key 1} (tab/tab :cmds {:label "Actions"})
            ^{:key 2} (tab/tab :rules {:label "Rules"})]]]]))))
