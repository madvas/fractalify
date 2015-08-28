(ns fractalify.fractals.components.control-text
  (:require [fractalify.components.form-text :as form-text]))

(def controls-debounce 700)

(defn control-text [params props & args]
  (into [] (apply concat
                  [form-text/form-text]
                  [params]
                  [(merge {:debounce controls-debounce} props)]
                  args)))
