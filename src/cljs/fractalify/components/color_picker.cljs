(ns fractalify.components.color-picker
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [reagent.core :as r]
            [schema.core :as s :include-macros true]
            [re-frame.core :as f]
            [fractalify.utils :as u]
            [cljs.core.async :refer [chan >! <!]]
            [material-ui.core :as ui]))

(def react-color-picker (r/adapt-react-class js/ColorPicker))
(def react-color-picker-panel (r/adapt-react-class (aget js/ColorPicker "Panel")))

(defn get-debounced-ch [change-ch props]
  (let [d (or (:debounce props) 0)]
    (apply u/debounce change-ch (if (number? d) [d] d))))

(defn parse-val [x]
  (-> (js->clj x)
      (select-keys ["color" "alpha"])
      vals vec))

(s/defn color-picker
  [db-path :- [s/Any] props]
  (let [color (f/subscribe db-path)
        change-ch (chan)
        debounced-chan (get-debounced-ch change-ch props)]
    (go
      (while true
        (let [val (<! debounced-chan)]
          (f/dispatch (conj db-path val)))))
    (fn []
      [react-color-picker
       (let [[hex-color alpha] @color]
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
            :on-change     #(go (>! change-ch (parse-val %)))}

           props))])))