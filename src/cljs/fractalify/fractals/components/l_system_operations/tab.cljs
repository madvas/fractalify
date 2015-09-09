(ns fractalify.fractals.components.l-system-operations.tab
  (:require [re-frame.core :as f]
            [material-ui.core :as ui]
            [schema.core :as s :include-macros true]
            [fractalify.fractals.schemas :as ch]
            [fractalify.fractals.components.l-system-operations.cmd :as cmd]
            [fractalify.fractals.components.l-system-operations.rule :as rule]))

(s/defn operations
  [type :- ch/operation-type]
  (let [items (f/subscribe [:form-item :fractals :l-system type])
        oper (condp = type
               :cmds cmd/cmd
               :rules rule/rule)]
    (fn []
      [:div.row.col-xs-12
       (for [k (keys @items)]
         ^{:key k}
         [:div.col-xs-12
          [oper k]])])))

(s/defn tab
  [type :- ch/operation-type
   props]
  [ui/tab props
   [operations type]
   [:div.row.center-xs
    [ui/icon-button
     {:icon-class-name "mdi mdi-plus-circle-outline"
      :icon-style      {:color (ui/palette-color :primary1-color)}
      :on-touch-tap    #(f/dispatch [:assoc-l-system-operation type])}]]])
