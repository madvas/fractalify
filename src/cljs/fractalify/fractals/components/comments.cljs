(ns fractalify.fractals.components.comments
  (:require [re-frame.core :as f]))

(defn comments []
  (let [comments (f/subscribe [:comments])]
    ))