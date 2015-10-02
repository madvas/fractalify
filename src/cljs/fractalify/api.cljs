(ns fractalify.api
  (:require [fractalify.utils :as u]
            [schema.core :as s :include-macros true]
            [fractalify.main.schemas :as ch]))

(s/defn fetch!
  [endpoint-key :- s/Keyword
   query-params :- ch/QueryParams
   on-success
   on-err]
  (println "Making real Ajax req" endpoint-key))

(s/defn send! [endpoint-key body-params on-success on-err]
  (println "Making real Ajax post" endpoint-key))

#_(defn request! [method endpoint-key query-params on-success on-err]
    )