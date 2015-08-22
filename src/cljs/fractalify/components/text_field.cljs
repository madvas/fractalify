(ns fractalify.components.text-field
  (:require
    [reagent.core :as r]
    [material-ui.core :as ui]
    [re-frame.core :as f]
    [schema.core :as s :include-macros true]
    [fractalify.utils :as u]
    [fractalify.validators :as v]))


(def style {:padding-bottom 13 :text-align "left"})
(def underline-style {:bottom 22})
(def error-style {:text-align "left"})

(s/defn text-field
  ([subscribe props]
    (text-field subscribe nil nil props))
  ([subscribe dispatch props]
    (text-field subscribe dispatch nil props))
  ([subscribe :- [s/Keyword]
    dispatch :- [s/Keyword]
    error-dispatch :- [s/Keyword]
    props :- {s/Keyword s/Any}]
    (let [value (f/subscribe subscribe)
          validators (into [] (concat
                                (when (:required props)
                                  [v/required])
                                (:validators props)))]
      (when-let [default-value (:default-value props)]
        (println dispatch)
        (f/dispatch (conj dispatch default-value)))
      (fn []
        (let [error-text (u/do-until-value @value validators)]
          (when error-dispatch
            (f/dispatch (conj error-dispatch error-text)))
          [ui/text-field (merge
                           {:value          @value
                            :errorText      error-text
                            :style          style
                            :underlineStyle underline-style
                            :errorStyle     error-style
                            }
                           (when dispatch
                             {:onChange       #(f/dispatch (conj dispatch (u/e-val %)))})
                           props)])))))