(ns fractalify.api.api
  (:require [schema.core :as s]))

(defn malformed-params? [schema params]
  (fn [_] (s/check schema params)))

(def base-resource
  {:available-media-types ["application/edn"]})


