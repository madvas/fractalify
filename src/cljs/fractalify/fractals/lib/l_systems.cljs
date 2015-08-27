(ns fractalify.fractals.lib.l-systems
  (:require [schema.core :as s :include-macros true]
            [fractalify.fractals.schemas :as schemas]))

(s/defn apply-rules :- s/Str
  [rules :- {s/Str s/Str}
   pattern :- s/Str]
  (apply str
         (replace rules pattern)))

(s/defn l-system :- s/Str
  [{:keys [start rules iterations]
    :as   params :- schemas/LSystem}]
  (nth
    (iterate
      (partial apply-rules (into {} rules)) start)
    iterations))
