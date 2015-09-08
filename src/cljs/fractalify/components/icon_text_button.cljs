(ns fractalify.components.icon-text-button
  (:require [material-ui.core :as ui]
            [reagent.core :as r]))

(def icon-style
  {:height         "100%"
   :display        :inline-block
   :vertical-align :middle
   :float          :left
   :padding-left   "12px"                                   ;
   :line-height    "36px"})

(defn icon-text-button [props]
  (let [btn (if (:raised props) ui/raised-button ui/flat-button)]
    [btn
     (r/merge-props {} props)
     [ui/font-icon
      {:style      (merge
                     icon-style
                     (:icon-style props)
                     (when (or (:primary props) (:secondary props))
                       {:color "#FFF"}))
       :class-name (or (:icon-class-name props) "")}]]))
