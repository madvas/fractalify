(ns fractalify.fractals.lib.l-systems
  (:require [schema.core :as s :include-macros true]
            [workers.turtle.schemas :as ch]
            [plumbing.core :as p]))

(s/defn apply-rules :- s/Str
  [rules :- {s/Str s/Str}
   pattern :- s/Str]
  (apply str
         (replace rules pattern)))

(s/defn l-system :- s/Str
  [l-system :- ch/LSystem]
  (p/letk [[start {rules {}} iterations] l-system]
    (nth
      (iterate
        (partial apply-rules (into {} (vals rules))) start)
      iterations)))
