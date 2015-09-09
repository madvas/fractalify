(ns fractalify.api
  (:require [fractalify.generators :as g]))

(defn request! [url query-params success-clb err-clb]
  (if goog.DEBUG
    (g/generate url query-params success-clb 100)
    (println "Making real Ajax req")))