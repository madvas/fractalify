(ns fractalify.generators
  (:require [schema.core :as s :include-macros true]
            [fractalify.utils :as u]
            [clojure.test.check.generators :as gen]
            [clojure.string :as str]
            [fractalify.main.schemas :as ch]))

(defmulti generator identity)
(defmethod generator :default [val]
  (u/mwarn "No generator method found for " val)
  nil)

(s/defn gen-sentence :- s/Str
  [word-size words-min words-max]
  (gen/generate
    (gen/fmap
      (partial str/join " ")
      (gen/vector (gen/resize word-size gen/string-alphanumeric) words-min words-max))))

(s/defn generate
  [endpoint-key :- s/Keyword
   query-params :- ch/QueryParams
   on-succ
   on-err
   ms :- s/Int]
  (let [res (generator endpoint-key query-params)
        f (if res on-succ on-err)]
    (u/set-timeout #(f res) ms)))