(ns fractalify.api
  (:require [fractalify.generators :as g]
            [fractalify.utils :as u]
            [schema.core :as s :include-macros true]
            [fractalify.main.schemas :as ch]))

(s/defn fetch!
  [endpoint-key :- s/Keyword
   query-params :- ch/QueryParams
   on-success
   on-err]
  (if goog.DEBUG
    (g/generate endpoint-key query-params on-success 100)
    (println "Making real Ajax req")))

(defn send! [endpoint-key query-params on-success on-err]
  (if goog.DEBUG
    (u/set-timeout #(on-err) 200)
    (println "Making real Ajax post")))

#_ (defn request! [method endpoint-key query-params on-success on-err]
  )