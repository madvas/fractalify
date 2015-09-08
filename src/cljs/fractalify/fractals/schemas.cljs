(ns fractalify.fractals.schemas
  (:require [schema.core :as s]
            [workers.turtle.schemas :as turtle-schemas]
            [fractalify.users.schemas :as user-schemas]))

(def o s/optional-key)

(def hex-color? (partial re-matches #"^#([A-Fa-f0-9]{6}|[A-Fa-f0-9]{3})$"))

(def Color [(s/one (s/pred hex-color?) "hex-color")
            (s/one s/Num "alpha")])

(def Base64Png (s/pred (partial re-matches #"^data:image/png;base64,.*")))

(def operation-type (s/enum :cmds :rules))
(def CanvasElement (s/pred (partial instance? js/HTMLCanvasElement)))
(def CanvasContext (s/pred (partial instance? js/CanvasRenderingContext2D)))

(def Canvas
  {:color      Color
   :bg-color   Color
   :line-width s/Num
   :size       s/Num
   (o :lines)  turtle-schemas/Lines})

(def Fractal
  {(o :l-system) turtle-schemas/LSystem
   (o :canvas)   Canvas
   (o :info)     {(o :title) s/Str
                  (o :desc)  s/Str}})

(def PublishedFractal
  (assoc Fractal :id s/Int
                 :src s/Str
                 :author user-schemas/User
                 :star-count s/Int
                 :starred-by-me s/Bool
                 (o :comments) [s/Any]))

(def FractalsSchema
  {:forms                   Fractal
   (o :fractal-detail)      PublishedFractal
   (o :l-system-generating) s/Bool
   :all-cmds                {s/Keyword s/Str}})