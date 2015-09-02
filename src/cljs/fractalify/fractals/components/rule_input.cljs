(ns fractalify.fractals.components.rule-input
  (:require [re-frame.core :as f]
            [fractalify.components.form-text :as form-text]
            [fractalify.fractals.components.control-text :as control-text]
            [material-ui.core :as ui]
            [fractalify.utils :as u]))

(def alphabet "ABCDEFGHIJKLMNOPQRSTUVWXYZ")
(def menu-items (map #(hash-map :payload %) alphabet))

(defn rule-input [rule-index]
  (let [variable (f/subscribe [:get-form-item :l-system :rules rule-index 0])]
    (fn []
      [:div.row
       [:div.col-xs-2.col-sm-2.col-md-1
        [control-text/control-text [:l-system :rules rule-index 0]
           {:floating-label-text "Var"}]
        #_ [ui/drop-down-menu
         {:menu-items     menu-items
          :display-member "payload"
          :value          @variable
          :on-change      #(f/dispatch [:set-form-item :l-system :rules rule-index 0 (u/e-val %)])}]]
       [:div.col-xs-5.col-sm-5.col-md-4
        [control-text/control-text [:l-system :rules rule-index 1]
         {:floating-label-text "Rule"}]]])))
