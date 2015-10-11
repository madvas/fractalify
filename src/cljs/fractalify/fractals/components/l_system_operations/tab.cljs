(ns fractalify.fractals.components.l-system-operations.tab
  (:require [re-frame.core :as f]
            [material-ui.core :as ui]
            [schema.core :as s :include-macros true]
            [fractalify.fractals.schemas :as ch]
            [fractalify.utils :as u]
            [fractalify.fractals.components.fractal-hints :as hints]))

(s/defn operations [items component]
  [:div.row.col-xs-12
   (for [item items]
     (let [k (key item)
           v (val item)]
       ^{:key k}
       [:div.col-xs-12
        [component k v]]))])

(s/defn tab
  [items
   type :- ch/operation-type
   component
   props]
  [ui/tab props
   [hints/chars-hint]
   [operations items component]
   [:div.row.center-xs
    [ui/icon-button
     {:icon-class-name "mdi mdi-plus-circle-outline"
      :icon-style      {:color (ui/palette-color :primary1-color)}
      :on-touch-tap    #(f/dispatch [:assoc-l-system-operation type])}]]])
