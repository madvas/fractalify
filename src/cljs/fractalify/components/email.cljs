(ns fractalify.components.email
  (:require [schema.core :as s :include-macros true]
            [fractalify.components.form-text :as form-text]
            [fractalify.validators :as v]))

(s/defn email
  [form :- s/Keyword
   form-item :- s/Keyword
   & props :- [{s/Keyword s/Any}]]
  [form-text/form-text [form form-item]
   (apply merge {:floating-label-text "Email"
                 :required            true
                 :validators          [v/email]}
          props)])
