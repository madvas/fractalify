(ns fractalify.fractals.components.l-system-operations.remove-btn
  (:require [re-frame.core :as f]
            [material-ui.core :as ui]
            [schema.core :as s :include-macros true]
            [fractalify.fractals.schemas :as ch]))

(s/defn remove-btn
  [type :- ch/operation-type
   key]
  [:div.col-xs-1.row.middle-xs
   [ui/icon-button
    {:icon-class-name "mdi mdi-close-circle-outline"
     :icon-style      {:color (ui/palette-color :accent1-color)}
     :on-touch-tap    #(f/dispatch [:dissoc-l-system-operation type key])}]])
