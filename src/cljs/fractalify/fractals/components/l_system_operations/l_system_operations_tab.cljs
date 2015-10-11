(ns fractalify.fractals.components.l-system-operations-tab
  (:require [re-frame.core :as f]
            [material-ui.core :as ui]
            [schema.core :as s :include-macros true]))

(def types (s/enum :cmds :rules))

(s/def operations
  [type :- types]
  (let [items (f/subscribe [:get-form-item :l-system type])]
    (fn []
      [:div.row.col-xs-12
       (for [k (keys @items)]
         ^{:key k}
         [:div.col-xs-12
          ])])))

(s/defn l-system-operations-tab
  [type :- types
   props]
  [ui/tab props
   [commands]
   [:div.row.center-xs
    [ui/icon-button
     {:icon-class-name "mdi mdi-plus-circle-outline"
      :icon-style      {:color (ui/palette-color :primary1-color)}
      :on-touch-tap    #(f/dispatch [:assoc-cmd])}]]])
