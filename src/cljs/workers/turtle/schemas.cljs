(ns workers.turtle.schemas
  (:require [schema.core :as s :include-macros true]
            [schema.coerce :as coerce]))

(def o s/optional-key)

(def coords {:x s/Num :y s/Num})
(def Line [(s/one coords "line-from")
           (s/one coords "line-to")])
(def Lines [(s/maybe Line)])

(def LSystem
  {(o :rules)   {s/Str [(s/one s/Str "rule-source")
                        (s/one s/Str "rule-product")]}
   :start       s/Str
   :angle       s/Num
   :iterations  (s/pred pos?)
   :line-length s/Num
   :start-angle s/Num
   :origin      coords
   (o :cmds)    {s/Str [(s/one s/Str "cmd-variable")
                        (s/one s/Keyword "cmd-action")]}})

(def Turtle
  {:position coords
   :angle    s/Num
   :stack    (s/pred list?)
   :lines    Lines})

(defn keyword->string [k]
  (name k))

(def +keyword-coercion+
  (merge
    {s/Str keyword->string}))


(defn l-system-coercion-matcher [schema]
  (or
    (+keyword-coercion+ schema)
    (coerce/+string-coercions+ schema)
    (coerce/keyword-enum-matcher schema)
    (coerce/set-matcher schema)))