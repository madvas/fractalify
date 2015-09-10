(ns fractalify.generators
  (:require [schema.core :as s :include-macros true]
            [fractalify.utils :as u]
            [clojure.test.check.generators :as gen]
            [clojure.string :as str]))

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

(s/defn add-generator!
  [url :- s/Str
   f :- (s/=> s/Any {s/Keyword s/Any})]
  (swap! *generators* #(assoc % url f)))

(s/defn generate [url query-params clb ms]
  #_ (u/set-timeout #(clb ((@*generators* url) query-params)) ms)
  (u/set-timeout #(clb (generator url query-params)) ms))