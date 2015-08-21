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
(def error-style {:text-align "left"})

(s/defn text-field
  [[form item] :- [s/Keyword]
   props :- {s/Keyword s/Any}]
  (let [value (f/subscribe [:get-form-item form item])
        validators (into [] (concat
                              (when (:required props)
                                [v/required])
                              (:validators props)))]
    (fn []
      (let [error-text (u/do-until @value validators)]
        (f/dispatch [:set-form-error form item error-text])
        [ui/text-field (merge
                         {:value          @value
                          :errorText      error-text
                          :style          style
                          :underlineStyle underline-style
                          :errorStyle     error-style
                          :onChange       #(f/dispatch
                                            [:set-form-item form item (u/e-val %)])}
                         props)]))))