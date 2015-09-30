(ns fractalify.readers
  (:require [clj-time.format :as tf]))

(defn object-reader [[symbol _ value]]
  (when (= (name symbol) "org.joda.time.DateTime")
    (tf/parse value)))

(defn get-readers []
  {'object #'fractalify.readers/object-reader})
