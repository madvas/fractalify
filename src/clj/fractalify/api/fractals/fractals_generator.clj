(ns fractalify.api.fractals.fractals-generator
  (:require [com.stuartsierra.component :as c]
            [fractalify.utils :as u]
            [plumbing.core :as p]
            [monger.collection :as mc]
            [fractalify.api.fractals.fractals-db :as fdb]
            [clojure.test.check.generators :as gen]
            [clj-time.core :as m]
            [schema.core :as s]
            [fractalify.fractals.schemas :as fch]))

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
                                                  {:username "admin"})))))))

  (stop [this]
    (dissoc this :fractals))
  )

(def new-fractals-generator ->FractalsGenerator)

(s/defn gen-fractal []
  (merge {:title (u/gen-sentence 7 1 3)
          :desc  (u/gen-sentence 10 3 10)
          :src   "http://res.cloudinary.com/hcjlrkjcu/image/upload/v1442987648/dragon-curve.png"}
         fch/dragon-curve))