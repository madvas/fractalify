(ns fractalify.fractals.db
  (:require [fractalify.utils :as u]
            [fractalify.fractals.schemas :as fch]))

(def default-db
  {:forms (merge
            (u/coerce-forms-with-defaults fch/FractalsForms)
            {:sidebar {:page     1
                       :sort     :star-count
                       :sort-dir -1
                       :limit    10}}
            fch/dragon-curve)})
