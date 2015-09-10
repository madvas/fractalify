(ns fractalify.fractals.components.l-system-operations.remove-btn
  (:require [re-frame.core :as f]
            [material-ui.core :as ui]
            [schema.core :as s :include-macros true]
            [fractalify.fractals.schemas :as ch]
            [fractalify.components.icon-button-remove :as icon-button-remove]))

(s/defn remove-btn
  [type :- ch/operation-type
   key]
  [:div.col-xs-1.row.middle-xs
   [icon-button-remove/icon-button-remove
    {:on-touch-tap    #(f/dispatch [:dissoc-l-system-operation type key])}]])
