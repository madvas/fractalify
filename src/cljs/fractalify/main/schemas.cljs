(ns fractalify.main.schemas
  (:require [schema.core :as s]))

(def o s/optional-key)

(def FormErros
  {(o :errors) {s/Keyword s/Any}})
