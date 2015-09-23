(ns fractalify.fractals.lib.renderer
  (:require [schema.core :as s :include-macros true]
            [monet.canvas :as c]
            [fractalify.styles :as y]
            [fractalify.workers.schemas :as turtle-schemas]
            [fractalify.fractals.schemas :as ch]
            [fractalify.utils :as u]
            [plumbing.core :as p]))

(def ^:dynamic *ctx* (atom nil))
(def ^:dynamic *canvas* (atom nil))

(defn init! [canvas-dom]
  (reset! *ctx* (c/get-context canvas-dom "2d"))
  (reset! *canvas* canvas-dom))

(defn clear-canvas [ctx w h]
  (c/clear-rect ctx {:x 0 :y 0 :w w :h h}))

(s/defn draw-bg
  [ctx
   canvas-dom
   [hex-color alpha] :- ch/Color]
  (-> ctx (c/fill-style hex-color)
      (c/alpha (/ alpha 100))
      (c/fill-rect {:x 0 :y 0 :w (aget canvas-dom "width") :h (aget canvas-dom "height")})))

(s/defn render-lines :- ch/CanvasContext
  [ctx :- ch/CanvasContext
   lines :- turtle-schemas/Lines]
  (doseq [line lines]
    (let [[from to] line]
      (-> ctx
          (c/move-to (:x from) (:y from))
          (c/line-to (:x to) (:y to)))))
  ctx)

(s/defn get-data-url :- ch/Base64Png []
  (.toDataURL (aget @*ctx* "canvas")))

(s/defn render!
  [canvas-dom :- ch/CanvasElement
   canvas :- ch/Canvas]
  (when (or (nil? @*ctx*) (not= canvas-dom (aget @*ctx* "canvas")))
    (init! canvas-dom))
  (let [[stroke-color stroke-alpha] (:color canvas)]
    (p/letk [[bg-color line-width lines] canvas]
      (-> @*ctx*
          (draw-bg canvas-dom ["#FFF" 100])
          (draw-bg canvas-dom bg-color)
          (c/stroke-style stroke-color)
          (c/alpha (/ stroke-alpha 100))
          (c/stroke-width line-width)
          c/begin-path
          (render-lines lines)
          c/stroke))))