(ns fractalify.main.schemas
  (:require [schema.core :as s]
            [cljs-time.core :as m]))

(def o s/optional-key)

(def DbPath [(s/cond-pre s/Keyword s/Int)])
(def QueryParams {s/Keyword s/Any})

(def FormErros
  {(o :errors) {s/Keyword s/Any}})

(def Date (s/pred m/date?))

(defn list-response [item-type]
  {:items       [item-type]
   :total-items s/Int})