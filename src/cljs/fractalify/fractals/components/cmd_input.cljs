(ns fractalify.fractals.components.cmd-input
  (:require [re-frame.core :as f]
            [fractalify.components.form-text :as form-text]
            [material-ui.core :as ui]
            [fractalify.utils :as u]
            [schema.core :as s :include-macros true]
            [clojure.string :as str]))

(def menu-items (map #(hash-map {:payload %
                                 :text (-> name
                                           str/capitalize)})
                     [:forward :left :right :push :pop ""]))

(s/defn cmd-input
  [cmd-key :- s/Str
   cmd-val :- s/Keyword]
  [:div.col-xs-12
   [:div.col-xs-6.col-md-3
    [form-text/form-text [:l-system :cmd-map {:key cmd-key}]
     {:floating-label-text "Action"}]]
   #_ [ui/select-field]])
