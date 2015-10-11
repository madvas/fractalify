(ns fractalify.fractals.schemas
  (:require [schema.core :as s]
            [fractalify.workers.schemas :as wch]
            [fractalify.users.schemas :as uch]
            [fractalify.main.schemas :as mch]
            [fractalify.utils :as u]))

(def o s/optional-key)
(def hex-color? (partial re-matches #"^#([A-Fa-f0-9]{6}|[A-Fa-f0-9]{3})$"))
(def FractalTitle s/Str)
(def FractalDesc s/Str)
(def CommentText s/Str)

(def Color
  (wch/with-coerce [(s/one (s/pred hex-color?) "hex-color")
                    (s/one s/Num "alpha")]
                   ["#000" 0]))

(def Base64Png (s/pred (partial re-matches
                                (re-pattern (str "^" u/base64-png-prefix ".*")))))

#_(def operation-type (s/enum :cmds :rules))

;(def operation-type (s/pred #(contains? #{:cmds :rules} %)))
(def operation-type s/Keyword)

(do
  #?@(:cljs [(def CanvasElement (s/pred (partial instance? js/HTMLCanvasElement)))
             (def CanvasContext (s/pred (partial instance? js/CanvasRenderingContext2D)))]))

(def FractalIdField
  {:id s/Str})

(def FractalPublishForm
  {:title FractalTitle
   :desc  FractalDesc})

(def Canvas
  {:color      Color
   :bg-color   Color
   :line-width s/Num
   :size       s/Num
   (o :lines)  wch/Lines})

(def PutFractalForm
  (merge
    FractalPublishForm
    {:l-system wch/LSystem
     :canvas   (dissoc Canvas (o :lines))
     :data-url Base64Png}))

#_(def FractalOrderTypes (s/enum :star-count :created))
#_(def FractalOrderTypes (s/pred #(contains? #{:star-count :created} %)))
(def FractalOrderTypes s/Keyword)

(def FractalListForm
  {(o :page)     s/Int
   (o :limit)    s/Int
   (o :sort)     FractalOrderTypes
   (o :sort-dir) s/Int
   (o :username) s/Str})

(def CommentForm {:text CommentText})

(def Comment
  {:id      s/Str
   :text    CommentText
   :author  uch/UserOther
   :fractal s/Str
   :created mch/Date})

(def CommentList (mch/list-response Comment))

(def PublishedFractal
  {:id            s/Str
   :title         FractalTitle
   :desc          FractalDesc
   :l-system      wch/LSystem
   :canvas        Canvas
   :src           s/Str
   :author        (s/cond-pre uch/UserOther s/Str)
   :star-count    s/Int
   :starred-by-me s/Bool
   :created       mch/Date
   (o :comments)  (s/maybe CommentList)})

(def PublishedFractalsList (mch/list-response PublishedFractal))

(def FractalsForms
  {:info     FractalPublishForm
   :l-system wch/LSystem
   :canvas   Canvas
   :comment  CommentForm
   :sidebar  FractalListForm})

(def FractalsSchema
  {:forms                   FractalsForms
   (o :fractal-detail)      PublishedFractal
   (o :fractals-sidebar)    PublishedFractalsList
   (o :fractals-user)       PublishedFractalsList
   (o :fractals-home)       {(o :star-count) PublishedFractalsList
                             (o :created)    PublishedFractalsList}
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
            {:sidebar {:page     1
                       :sort     :star-count
                       :sort-dir -1
                       :limit    10}}
            dragon-curve)})