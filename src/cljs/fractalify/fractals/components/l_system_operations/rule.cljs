(ns fractalify.fractals.components.rule
  (:require [re-frame.core :as f]
            [fractalify.fractals.components.control-text :as control-text]
            [material-ui.core :as ui]
            [fractalify.utils :as u]
            [fractalify.fractals.components.l-system-operations.remove-btn :as remove-btn]))

(def alphabet "ABCDEFGHIJKLMNOPQRSTUVWXYZ")
(def menu-items (map #(hash-map :payload %) alphabet))

(defn rule [k]
  (fn []
    [:div.row
     [:div.col-xs-3
      [control-text/control-text [:l-system :rules k 0]
       {:floating-label-text "Variable"}]]
     [:div.col-xs-8
      [control-text/control-text [:l-system :rules k 1]
       {:floating-label-text "Rule"}]]
     [remove-btn/remove-btn :rules k]]))
