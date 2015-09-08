(ns fractalify.components.password
  (:require [schema.core :as s :include-macros true]
            [fractalify.components.form-text :as form-text]
            [fractalify.validators :as v]))

(s/defn password
  [floating-label-text :- s/Str
   path :- [s/Any]
   props]
  [form-text/form-text path
   (merge {:floating-label-text floating-label-text
           :type                "password"
           :required            true
           :validators          [v/password]}
          props)])
