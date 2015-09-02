(ns fractalify.components.form-text
  (:require-macros [fractalify.tracer-macros :refer [trace-views]])
  (:require
    [schema.core :as s :include-macros true]
    [fractalify.components.text-field :as text-field]))

(s/defn form-text
  [params :- [s/Any]
   props :- {s/Keyword s/Any}]
  [text-field/text-field
   (into [:get-form-item] params)
   (into [:set-form-item] params)
   (into [:set-form-error] params)
   props])