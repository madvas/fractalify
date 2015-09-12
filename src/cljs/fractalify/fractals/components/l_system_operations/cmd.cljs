(ns fractalify.fractals.components.l-system-operations.cmd
  (:require [fractalify.utils :as u]
            [schema.core :as s :include-macros true]
            [fractalify.components.form-select :as form-select]
            [fractalify.fractals.components.l-system-operations.remove-btn :as remove-btn]
            [fractalify.fractals.components.control-text :as control-text]))

(def all-cmds
  [{:payload :forward :text "Forward"}
   {:payload :left :text "Rotate Left"}
   {:payload :right :text "Rotate Right"}
   {:payload :push :text "Push Position"}
   {:payload :pop :text "Pop Position"}
   {:payload :default :text "No Action"}])

(defn cmd [k]
  [:div.row
   [:div.col-xs-3
    [control-text/control-text [:l-system :cmds k 0]
     {:floating-label-text "Variable"}]]
   [:div.col-xs-8
    [form-select/form-select [:fractals :l-system :cmds k 1]
     {:floating-label-text "Action"
      :menu-items          all-cmds}]]
   [remove-btn/remove-btn :cmds k]])