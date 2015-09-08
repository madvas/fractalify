(ns fractalify.components.email
  (:require [schema.core :as s :include-macros true]
            [fractalify.components.form-text :as form-text]
            [fractalify.validators :as v]))

(s/defn email
  [path :- [s/Any]
   & props :- [{s/Keyword s/Any}]]
  [form-text/form-text path
   (apply merge {:floating-label-text "Email"
                 :required            true
                 :validators          [v/email]}
          props)])
