(ns fractalify.generators
  (:require [schema.core :as s :include-macros true]
            [fractalify.utils :as u]
            [clojure.test.check.generators :as gen]
            [clojure.string :as str]
            [fractalify.main.schemas :as ch]))

(def ^:dynamic *generators* (atom {}))

(defmulti generator identity)
#_ (defmethod generator :default [val]
  (u/merror "No generator method found for " val))

(s/defn gen-sentence :- s/Str
  [word-size words-min words-max]
  (gen/generate
    (gen/fmap
      (partial str/join " ")
      (gen/vector (gen/resize word-size gen/string-alphanumeric) words-min words-max))))

(s/defn generate
  [endpoint-key :- s/Keyword
   query-params :- ch/QueryParams
   callback
   ms :- s/Int]
  #_ (u/set-timeout #(callback ((@*generators* endpoint-key) query-params)) ms)
  (u/set-timeout #(callback (generator endpoint-key query-params)) ms))