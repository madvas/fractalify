(ns fractalify.api.fractals.fractals-generator
  (:require [fractalify.db-generator :only [Generator]]
            [com.stuartsierra.component :as c]
            [fractalify.utils :as u]
            [plumbing.core :as p]
            [monger.collection :as mc]
            [fractalify.api.fractals.fractals-db :as fdb]
            [clojure.test.check.generators :as gen]
            [clj-time.core :as m]
            [schema.core :as s]))

(declare gen-fractal)
(def total-items-generated 30)

(defrecord FractalsGenerator []
  c/Lifecycle
  (start [this]
    (p/letk [[db] (:db-server this)
             [users] (:users-generator this)]
      (mc/remove db fdb/coll)
      (mc/remove db fdb/coll-comments)
      (assoc this
        :fractals
        (doall
          (take total-items-generated
                (repeatedly
                  #(fdb/fractal-insert-and-return db (gen-fractal)
                                                  (rand-nth users))))))))

  (stop [this]
    (dissoc this :fractals))
  )

(def new-fractals-generator ->FractalsGenerator)

(s/defn gen-fractal []
  {:title    (u/gen-sentence 7 1 3)
   :desc     (u/gen-sentence 10 3 10)
   :src      "http://res.cloudinary.com/hcjlrkjcu/image/upload/v1442987648/dragon-curve.png"
   :l-system {:rules       {1 ["X" "X+YF"]
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