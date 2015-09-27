(ns fractalify.fractals.schemas
  (:require [schema.core :as s]
            [fractalify.workers.schemas :as wch]
            [fractalify.users.schemas :as uch]
            [fractalify.main.schemas :as mch]))

(def o s/optional-key)

(def hex-color? (partial re-matches #"^#([A-Fa-f0-9]{6}|[A-Fa-f0-9]{3})$"))

(def Color
  (wch/with-coerce
    [(s/one (s/pred hex-color?) "hex-color")
     (s/one s/Num "alpha")] ["#000" 0]))

(def Base64Png (s/pred (partial re-matches #"^data:image/png;base64,.*")))

(def operation-type (s/enum :cmds :rules))

(do
  #?@(:cljs [(def CanvasElement (s/pred (partial instance? js/HTMLCanvasElement)))
             (def CanvasContext (s/pred (partial instance? js/CanvasRenderingContext2D)))]))

(def FractalsQueryParams
  {(o :page)     s/Int
   (o :limit)    s/Int
   (o :sort)     s/Keyword
   (o :sort-dir) s/Int
   (o :username) s/Str})

(def Canvas
  {:color      Color
   :bg-color   Color
   :line-width s/Num
   :size       s/Num
   (o :lines)  wch/Lines})

(def Fractal
  {:l-system wch/LSystem
   :canvas   Canvas
   :info     {:title s/Str
              :desc  s/Str}})

(def Comment
  {:id      s/Int
   :text    s/Str
   :author  uch/User
   :created mch/Date})

(def PublishedFractal
  {:id            s/Int
   :title         s/Str
   :desc          s/Str
   :l-system      wch/LSystem
   :canvas        Canvas
   :src           s/Str
   :author        uch/User
   :star-count    s/Int
   :starred-by-me s/Bool
   :created       mch/Date
   (o :comments)  [Comment]})

(def FractalOrderTypes (wch/with-coerce (s/enum :best :recent) :best))

(def PublishedFractalsList (mch/list-response PublishedFractal))

(def FractalsForms
  (merge
    Fractal
    {:comment {:text s/Str}
     :sidebar {:page  s/Int
               :order FractalOrderTypes
               :limit s/Int}}))

(def FractalsSchema
  {:forms                   FractalsForms
   (o :fractal-detail)      PublishedFractal
   (o :fractals-sidebar)    PublishedFractalsList
   (o :fractals-user)       PublishedFractalsList
   (o :fractals-home)       {(o :best)   PublishedFractalsList
                             (o :recent) PublishedFractalsList}
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
            (mch/coerce-forms-with-defaults FractalsForms)
            {:sidebar {:page  1
                       :order :best
                       :limit 10}}
            dragon-curve)})