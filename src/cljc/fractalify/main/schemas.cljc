(ns fractalify.main.schemas
  (:require [schema.core :as s]
    #?@(:clj  [
            [clj-time.core :as m]]
        :cljs [[cljs-time.core :as m]])))

(def o s/optional-key)

(def DbPath [(s/cond-pre s/Keyword s/Int)])
(def QueryParams {s/Keyword s/Any})

(def FormErros
  {(o :errors) {s/Keyword s/Any}})

(def Date (s/pred #(satisfies? m/DateTimeProtocol %)))

(defn list-response [item-type]
  {:items       [item-type]
   :total-items s/Int})