(ns fractalify.fractals.schemas
  (:require [schema.core :as s]
            [fractalify.workers.schemas :as wch]
            [fractalify.users.schemas :as uch]
            [fractalify.main.schemas :as mch]
            [fractalify.utils :as u]
            [clojure.set :as set]))

(def o s/optional-key)
(s/defschema hex-color? (partial re-matches #"^#([A-Fa-f0-9]{6}|[A-Fa-f0-9]{3})$"))
(def FractalTitle s/Str)
(def FractalDesc s/Str)
(def CommentText s/Str)

(s/defschema Color
  (wch/with-coerce [(s/one (s/pred hex-color?) "hex-color")
                    (s/one s/Num "alpha")]
                   ["#000" 0]))

(s/defschema Base64Png (s/pred (partial re-matches
                                        (re-pattern (str "^" u/base64-png-prefix ".*")))))

(s/defschema operation-type (s/enum :cmds :rules))

(do
  #?@(:cljs [(s/defschema CanvasElement (s/pred (partial instance? js/HTMLCanvasElement)))
             (s/defschema CanvasContext (s/pred (partial instance? js/CanvasRenderingContext2D)))]))

(s/defschema FractalIdField
  {:id s/Str})

(s/defschema FractalPublishForm
  {:title FractalTitle
   :desc  FractalDesc})

(s/defschema Canvas
  {:color      Color
   :bg-color   Color
   :line-width s/Num
   :size       s/Num
   (o :lines)  wch/Lines})

(s/defschema RenderableCanvas
  (-> Canvas
      (set/rename-keys {(o :lines) :lines})
      (merge mch/FormErros)))

(s/defschema PutFractalForm
  (merge
    FractalPublishForm
    {:l-system wch/LSystem
     :canvas   (dissoc Canvas (o :lines))
     :data-url Base64Png}))

(s/defschema FractalOrderTypes (s/enum :star-count :created))

(s/defschema FractalListForm
  {(o :page)     s/Int
   (o :limit)    s/Int
   (o :sort)     FractalOrderTypes
   (o :sort-dir) s/Int
   (o :username) s/Str})

(s/defschema CommentForm {:text CommentText})

(s/defschema Comment
  {:id      s/Str
   :text    CommentText
   :author  uch/UserOther
   :fractal s/Str
   :created mch/Date})

(s/defschema CommentList (mch/list-response Comment))

(s/defschema PublishedFractal
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

(s/defschema PublishedFractalsList (mch/list-response PublishedFractal))

(s/defschema FractalsForms
  {:info     FractalPublishForm
   :l-system wch/LSystem
   :canvas   Canvas
   :comment  CommentForm
   :sidebar  FractalListForm})

(s/defschema FractalsSchema
  {:forms                   FractalsForms
   (o :fractal-detail)      PublishedFractal
   (o :fractals-sidebar)    PublishedFractalsList
   (o :fractals-user)       PublishedFractalsList
   (o :fractals-home)       {(o :star-count) PublishedFractalsList
                             (o :created)    PublishedFractalsList}
   (o :l-system-generating) s/Bool
   (o :turtle-worker)       #?(:cljs (s/maybe (s/pred (partial instance? js/Worker)))
                               :clj  s/Any)})

(def dragon-curve
  {:l-system {:rules       {1 ["X" "X+YF"]
                            2 ["Y" "FX-Y"]}
              :angle       90
              :start       "FX"
              :iterations  12
              :line-length 6
              :origin      {:x 500 :y 400}
              :start-angle 90
              :cmds        {1 ["F" :forward]
                            2 ["+" :left]
                            3 ["-" :right]
                            4 ["[" :push]
                            5 ["]" :pop]}}
   :canvas   {:bg-color   ["#FFF" 100]
              :size       700
              :color      ["#00bcd4" 100]
              :line-width 3}})