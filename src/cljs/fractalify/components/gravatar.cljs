(ns fractalify.components.gravatar
  (:require [fractalify.utils :as u]))

(defn gravatar [md5 size]
  [:img {:src (u/gravatar-url md5 size)}])