(ns fractalify.fractals.components.control-text
  (:require [fractalify.components.form-text :as form-text]
            [fractalify.utils :as u]))

(def controls-debounce 700)

(defn control-input [f value floating-label-text path props]
  [f value floating-label-text
   (into [:fractals] path)
   (merge {:debounce controls-debounce} props)])

(defn control-text [& args]
  (apply control-input form-text/text args))

(defn control-number [& args]
  (apply control-input form-text/number args))
