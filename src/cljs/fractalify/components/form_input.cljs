(ns fractalify.components.form-input
  (:require-macros [fractalify.tracer-macros :refer [trace-views]])
  (:require
    [schema.core :as s :include-macros true]
    [fractalify.components.text-field :as text-field]
    [fractalify.utils :as u]
    [fractalify.validators :as v]))

(s/defn text
  [value floating-label-text path props]
  [text-field/text-field
   value
   floating-label-text
   (into [:form-item] path)
   (into [:form-error] path)
   props])

(defn number [& args]
  (u/with-default-props
    text
    {:type "number"}
    args))

(defn password [& args]
  (u/with-default-props
    text
    {:type       "password"
     :required   true
     :validators [v/password]}
    args))

(defn email [& args]
  (u/with-default-props
    text
    {:required   true
     :validators [v/email]}
    args))