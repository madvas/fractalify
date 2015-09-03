(ns fractalify.fractals.components.cmd-input
  (:require [re-frame.core :as f]
            [fractalify.components.form-text :as form-text]
            [material-ui.core :as ui]
            [fractalify.utils :as u]
            [schema.core :as s :include-macros true]
            [clojure.string :as str]
            [fractalify.components.form-select :as form-select]))

(def menu-items (map #(hash-map :payload %
                                :text (-> (name %)
                                          str/capitalize))
                     [:forward :left :right :push :pop ""]))

(defn cmd-input
  [key]
  [:div.row
   [:div.col-xs-3
    [form-text/form-text [:l-system :cmds key 0]
     {:floating-label-text "Variable"}]]
   [:div.col-xs-8
    [form-select/form-select
     [:l-system :cmds key 1]
     [:config :fractals :all-cmds]
     {:floating-label-text "Action"}]]
   [:div.col-xs-1.row.middle-xs
    [ui/icon-button
     {:icon-class-name "mdi mdi-close-circle-outline"
      :on-touch-tap    #(f/dispatch [:dissoc-cmd key])}]]])
