(ns fractalify.components.text-field
  (:require-macros [fractalify.tracer-macros :refer [trace-views]])
  (:require
    [reagent.core :as r]
    [fractalify.tracer]
    [material-ui.core :as ui]
    [re-frame.core :as f]
    [schema.core :as s :include-macros true]
    [fractalify.utils :as u]
    [fractalify.validators :as v]
    [fractalify.styles :as y]))


(def style (merge y/w-100 {:padding-bottom 13 :text-align "left"}))
(def underline-style {:bottom 22})
(def error-style {:text-align "left"})

(def params-format [(s/either s/Keyword s/Int)])

(declare text-field)

(s/defn text-field
  ([subscribe props]
    (text-field subscribe nil nil props))
  ([subscribe dispatch props]
    (text-field subscribe dispatch nil props))
  ([subscribe :- params-format
    dispatch :- params-format
    error-dispatch :- params-format
    props :- {s/Keyword s/Any}]
    (let [value (f/subscribe subscribe)
          validators (into [] (concat
                                (when (:required props)
                                  [v/required])
                                (:validators props)))]
      (when-let [default-value (:default-value props)]
        (f/dispatch (conj dispatch default-value)))
      (fn []
        (let [error-text (u/validate-until-error @value validators)]
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
                             {:onChange
                              #(f/dispatch
                                (conj dispatch
                                      (if (= (:type props) "number")
                                        (u/parse-float (u/e-val %))
                                        (u/e-val %))))})
                           props)])))))