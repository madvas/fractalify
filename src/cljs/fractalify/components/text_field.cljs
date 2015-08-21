(ns fractalify.components.text-field
  (:require
    [reagent.core :as r]
    [material-ui.core :as ui]
    [re-frame.core :as f]
    [schema.core :as s :include-macros true]
    [fractalify.utils :as u]
    [fractalify.validators :as v]))


(def style {:padding-bottom 13})
(def underline-style {:bottom 22})

(s/defn text-field
  [subscription :- s/Keyword
   dispatch :- [s/Keyword]
   props :- {s/Keyword s/Any}]
  (let [value (f/subscribe [subscription])
        validators (into [] (concat
                              (when (:required props)
                                [v/required])
                              (:validators props)))]
    (fn []
      (let [error-text (u/do-until @value validators)]
        (f/dispatch (into [] (concat [:set-form-error] dispatch [error-text])))
        [ui/text-field (merge
                         {:value          @value
                          :errorText      error-text
                          :style          style
                          :underlineStyle underline-style
                          :onChange       #(f/dispatch
                                            (into [] (concat [:set-form-item] dispatch [(u/e-val %)])))}
                         props)]))))