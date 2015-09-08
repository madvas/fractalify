(ns fractalify.fractals.components.control-text
  (:require [fractalify.components.form-text :as form-text]
            [fractalify.utils :as u]))

(def controls-debounce 700)

(defn control-text [params props & args]
  (u/concat-vec (apply concat
                       [form-text/form-text]
                       [(into [:fractals] params)]
                       [(merge {:debounce controls-debounce} props)]
                       args)))
