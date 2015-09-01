(ns fractalify.fractals.schemas
  (:require [schema.core :as s]))

(def o s/optional-key)

(def CanvasElement (s/pred (partial instance? js/HTMLCanvasElement)))
(def CanvasContext (s/pred (partial instance? js/CanvasRenderingContext2D)))

