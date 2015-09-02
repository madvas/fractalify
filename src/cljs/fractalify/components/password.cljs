(ns fractalify.components.password
  (:require [schema.core :as s :include-macros true]
            [fractalify.components.form-text :as form-text]
            [fractalify.validators :as v]))

(s/defn password
  [floating-label-text :- s/Str
   form :- s/Keyword
   form-item :- s/Keyword
   & props :- [{s/Keyword s/Any}]]
  [form-text/form-text [form form-item]
   (apply merge {:floating-label-text floating-label-text
                 :type                "password"
                 :required            true
                 :validators          [v/password]}
          props)])
