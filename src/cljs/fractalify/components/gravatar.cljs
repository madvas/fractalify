(ns fractalify.components.gravatar)

(defn gravatar [url size]
  [:img {:src (str url "?s=" size)}])