(ns fractalify.main.schemas
  (:require [schema.core :as s]))

(def o s/optional-key)

(def DbPath [(s/cond-pre s/Keyword s/Int)])
(def QueryParams {s/Keyword s/Any})

(def FormErros
  {(o :errors) {s/Keyword s/Any}})
