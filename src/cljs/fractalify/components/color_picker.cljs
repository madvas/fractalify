(ns fractalify.components.color-picker
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [reagent.core :as r]
            [schema.core :as s :include-macros true]
            [re-frame.core :as f]
            [fractalify.utils :as u]
            [cljs.core.async :refer [chan >! <!]]
            [material-ui.core :as ui]
            [fractalify.main.schemas :as ch]
            [fractalify.fractals.schemas :as fch]))

(def react-color-picker (r/adapt-react-class js/ColorPicker))
(def react-color-picker-panel (r/adapt-react-class (aget js/ColorPicker "Panel")))

(defn parse-val [x]
  (-> (js->clj x)
      (select-keys ["color" "alpha"])
      vals vec))

(defn on-change [path val]
  (f/dispatch (conj path val)))

(s/defn color-picker
  [color :- fch/Color
   path :- ch/DbPath
   props]
  (let [debounced-change (u/debounce #(on-change path %) (:debounce props))]
    (fn [color]
      [react-color-picker
       (let [[hex-color alpha] color]
         (merge
           {:default-color hex-color
            :alpha         alpha
            :animation     "slide-up"
            :trigger       (r/as-element
                             [ui/icon-button
                              (merge
                                {:icon-class-name  "mdi mdi-eyedropper-variant"
                                 :class-name       "colorpicker-trigger-btn"
                                 :icon-style       {:color (ui/color :grey700)}
                                 :tooltip          "Choose Color"
                                 :tooltip-position "top-center"}
                                (:trigger-props props))])
            :on-change     #(-> % parse-val debounced-change)}

           props))])))