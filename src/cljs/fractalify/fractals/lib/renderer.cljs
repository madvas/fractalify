(ns fractalify.fractals.lib.renderer
  (:require [schema.core :as s :include-macros true]
            [monet.canvas :as c]
            [fractalify.styles :as y]
            [workers.turtle.schemas :as turtle-schemas]
            [fractalify.fractals.schemas :as ch]
            [fractalify.utils :as u]))

(def ^:dynamic *ctx* (atom nil))

(defn init! [canvas-dom]
  (reset! *ctx* (c/get-context canvas-dom "2d")))

(defn clear-canvas [ctx]
  (c/clear-rect ctx {:x 0 :y 0 :w (:width y/canvas-size) :h (:height y/canvas-size)}))

(s/defn render-lines :- ch/CanvasContext
  [ctx :- ch/CanvasContext
   lines :- turtle-schemas/Lines]
  (doseq [line lines]
    (let [[from to] line]
      (-> ctx
          (c/move-to (:x from) (:y from))
          (c/line-to (:x to) (:y to))
          )))
  ctx)

(s/defn render!
  [canvas-dom :- ch/CanvasElement
   lines :- turtle-schemas/Lines]
  (when (or (nil? @*ctx*) (not= canvas-dom (aget @*ctx* "canvas")))
    (init! canvas-dom))
  (-> @*ctx*
      clear-canvas
      (c/stroke-style "#000")
      c/begin-path
      (render-lines lines)
      c/stroke))