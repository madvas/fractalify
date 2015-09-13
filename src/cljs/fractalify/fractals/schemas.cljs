(ns fractalify.fractals.schemas
  (:require [schema.core :as s]
            [workers.turtle.schemas :as turtle-schemas]
            [fractalify.users.schemas :as uch]
            [fractalify.main.schemas :as mch]))

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

(def Comment
  {:id      s/Int
   :text    s/Str
   :author  uch/User
   :created mch/Date})

(def PublishedFractal
  {:id            s/Int
   :title         s/Str
   :desc          s/Str
   :l-system      turtle-schemas/LSystem
   :canvas        Canvas
   :src           s/Str
   :author        uch/User
   :star-count    s/Int
   :starred-by-me s/Bool
   :created       mch/Date
   (o :comments)  [Comment]})

(def FractalsSchema
  {:forms                   (merge
                              Fractal
                              {(o :comment) {(o :text) s/Str}
                               :sidebar     {:page  s/Int
                                             :order (s/enum :best :recent)
                                             :limit s/Int}})
   (o :fractal-detail)      PublishedFractal
   (o :fractals-sidebar)    {:items       [PublishedFractal]
                             :total-items s/Int}
   (o :l-system-generating) s/Bool})

(def dragon-curve
  {:l-system {:rules       {1 ["X" "X+YF"]
                            2 ["Y" "FX-Y"]}
              :angle       90
              :start       "FX"
              :iterations  12
              :line-length 6
              :origin      {:x 300 :y 300}
              :start-angle 90
              :cmds        {1 ["F" :forward]
                            2 ["+" :left]
                            3 ["-" :right]
                            4 ["[" :push]
                            5 ["]" :pop]}}
   :canvas   {:bg-color   ["#FFF" 100]
              :size       600
              :color      ["#000" 100]
              :line-width 1}})

(def default-db
  {:forms (merge
            {:sidebar {:page  1
                       :order :best
                       :limit 10}}
            dragon-curve)})