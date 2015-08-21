(ns fractalify.validators)

(defn required [val]
  (when (empty? val) "This field is required"))

(defn alphanumeric [val]
  (when-not (re-matches #"[a-zA-Z]\w*" val) "Only alphanumeric characters are allowed"))