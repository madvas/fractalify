(ns fractalify.api
  (:require [fractalify.generators :as g]
            [fractalify.utils :as u]
            [schema.core :as s :include-macros true]
            [fractalify.main.schemas :as ch]
            [re-frame.core :as f]))

(s/defn fetch!
  [endpoint-key :- s/Keyword
   query-params :- ch/QueryParams
   on-success
   on-err]
  (if goog.DEBUG
    (g/generate endpoint-key query-params on-success on-err 100)
    (println "Making real Ajax req")))

(s/defn send! [endpoint-key body-params on-success on-err]
  (if goog.DEBUG
    (g/generate endpoint-key body-params on-success on-err 100)
    (println "Making real Ajax post")))

#_(defn request! [method endpoint-key query-params on-success on-err]
    )