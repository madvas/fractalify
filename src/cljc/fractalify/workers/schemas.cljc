(ns fractalify.workers.schemas
  (:require [schema.core :as s :include-macros true]
            [schema.coerce :as coerce]))

(s/defrecord DefaultCoerceSchema
  [schema :- (s/protocol s/Schema) default-value]
  s/Schema
  (spec [this] (s/spec schema))

  (explain [this] (cons 'default-coerce schema)))

(defn with-coerce [schema default-value]
  (DefaultCoerceSchema. schema default-value))

(def o s/optional-key)

(def coords {:x s/Num :y s/Num})
(def Line [(s/one coords "line-from")
           (s/one coords "line-to")])
(def Lines [(s/maybe Line)])

(def Rule [(s/one s/Str "rule-variable")
           (s/one s/Str "rule-product")])

(def Cmd [(s/one s/Str "cmd-variable")
          (s/one s/Keyword "cmd-action")])

(def LSystem
  {(o :rules)   {s/Int Rule}
   :start       s/Str
   :angle       s/Num
   :iterations  (with-coerce (s/pred pos?) 1)
   :line-length s/Num
   :start-angle s/Num
   :origin      (with-coerce coords {:x 0 :y 0})
   (o :cmds)    {s/Int Cmd}})

(def Turtle
  {:position coords
   :angle    s/Num
   :stack    (s/pred list?)
   :lines    Lines})

(defn keyword->string [k]
  (name k))

(defn keyword->int [k]
  (int (keyword->string k)))

(def +keyword-coercion+
  (merge
    {s/Str keyword->string
     s/Int keyword->int}))

(defn l-system-coercion-matcher [schema]
  (or
    (+keyword-coercion+ schema)
    (coerce/+string-coercions+ schema)
    (coerce/keyword-enum-matcher schema)
    (coerce/set-matcher schema)))