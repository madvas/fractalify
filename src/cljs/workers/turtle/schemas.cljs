(ns workers.turtle.schemas
  (:require [schema.core :as s]))

(def o s/optional-key)

(def coords {:x s/Num :y s/Num})
(def Line [(s/one coords "line-from")
           (s/one coords "line-to")])
(def Lines [(s/maybe Line)])

(def LSystem
  {:rules          (s/maybe [[(s/one s/Str "rule-source")
                              (s/one s/Str "rule-product")]])
   :start          s/Str
   :angle          s/Num
   :iterations     (s/pred pos?)
   :line-length    s/Num
   :start-angle    s/Num
   :origin         coords})

(def Turtle
  {:position coords
   :angle    s/Num
   :stack    (s/pred list?)
   :lines    Lines})

