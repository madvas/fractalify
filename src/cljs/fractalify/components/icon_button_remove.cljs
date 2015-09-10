(ns fractalify.components.icon-button-remove
  (:require [material-ui.core :as ui]
            [reagent.core :as r]))

(defn icon-button-remove [props]
  [ui/icon-button
   (r/merge-props
     {:icon-class-name "mdi mdi-close-circle-outline"
      :icon-style      {:color (ui/palette-color :accent1-color)}} props)])