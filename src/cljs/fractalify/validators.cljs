(ns fractalify.validators
  (:require [fractalify.utils :as u]))

(defn required [val]
  (when (empty? val) "This field is required"))

(defn alphanumeric [val]
  (when-not (re-matches #"[a-zA-Z]\w*" val) "Only alphanumeric characters are allowed"))

(defn passwords-match [first]
  (fn [second]
    (when (not= first second) "Passwords must match")))

(defn email [val]
  (when-not (re-matches #"\S+@\S+.\S+"
                        val) "This is invalid email address"))

(defn length
  ([min]
   (fn [val]
     (when (> min (count val)) (str "Please enter at least " min " characters"))))
  ([min max]
   (fn [val]
     (let [c (count val)]
       (when (or (> min c)
                 (< max c)) (if (= min 0)
                              (str "Please enter string up to " max " characters")
                              (str "Please enter string between " min " and " max " characters")))))))

(def password (length 6))