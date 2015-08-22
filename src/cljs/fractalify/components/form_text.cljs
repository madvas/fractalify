(ns fractalify.components.form-text
  (:require
    [schema.core :as s :include-macros true]
    [fractalify.components.text-field :as text-field]))


(s/defn form-text
  [[form item] :- [s/Keyword]
   props :- {s/Keyword s/Any}]
  [text-field/text-field
   [:get-form-item form item]
   [:set-form-item form item]
   [:set-form-error form item]
   props])