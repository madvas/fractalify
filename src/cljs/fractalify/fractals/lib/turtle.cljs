(ns fractalify.fractals.lib.turtle
  (:require-macros [clairvoyant.core :refer [trace-forms]])
  (:require [fractalify.fractals.lib.l-systems :as l]
            [monet.canvas :as c]
            [fractalify.tracer :refer [tracer]]
            [fractalify.utils :as u]
            [fractalify.styles :as y]
            [schema.core :as s :include-macros true]))


(def ^:dynamic *ctx* (atom nil))

(def cmd-map {"F" :forward
              "+" :left
              "-" :right
              "[" :push
              "]" :pop})

(declare render!)
(declare init!)

(trace-forms
  {:tracer (tracer :color "orange")}

  (s/defn render!
    [canvas-dom :- (s/pred (partial instance? js/HTMLCanvasElement))
     l-system :- {(s/required-key :result-cmds) s/Str s/Any s/Any}]
    (when-not @*ctx*
      (init! canvas-dom))
    #_(c/clear-rect @*ctx* {:x 0 :y 0 :w 500 :h 500})
    #_(c/fill-style @*ctx* "#FF0")
    #_(c/fill-rect @*ctx* {:x 0 :y 0 :w 500 :h 100}))

  (defn init! [canvas-dom]
    (reset! *ctx* (c/get-context canvas-dom "2d")))

  #_(defn gen-coords [grammar env]
      (let [origin {:x     (first (:origin env))
                    :y     (second (:origin env))
                    :angle (:start-angle env)}
            turtle {:current-pos origin :stack '() :lines []}
            commands (gen-commands grammar (:n-productions env))]
        (:lines
          (reduce exec-cmd turtle commands)))))
