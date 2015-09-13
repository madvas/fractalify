(ns fractalify.fractals.components.l-system-operations.cmd
  (:require [fractalify.utils :as u]
            [schema.core :as s :include-macros true]
            [fractalify.components.form-select :as form-select]
            [fractalify.fractals.components.l-system-operations.remove-btn :as remove-btn]
            [fractalify.fractals.components.control-text :as control-text]
            [workers.turtle.schemas :as ch]))

(def all-cmds
  [{:payload :forward :text "Forward"}
   {:payload :left :text "Rotate Left"}
   {:payload :right :text "Rotate Right"}
   {:payload :push :text "Push Position"}
   {:payload :pop :text "Pop Position"}
   {:payload :default :text "No Action"}])

(s/defn cmd
  [k :- s/Int
   cmd-item :- ch/Cmd]
  (let [[var action-val] cmd-item]
    [:div.row
     [:div.col-xs-3
      [control-text/control-text var "Variable" [:l-system :cmds k 0]]]
     [:div.col-xs-8
      [form-select/form-select action-val "Action" [:fractals :l-system :cmds k 1]
       {:menu-items all-cmds}]]
     [remove-btn/remove-btn :cmds k]]))