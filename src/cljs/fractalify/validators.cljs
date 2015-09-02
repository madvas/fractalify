(ns fractalify.validators)

(defn required [val]
  (when (empty? val) "This field is required"))

(defn alphanumeric [val]
  (when-not (re-matches #"[a-zA-Z]\w*" val) "Only alphanumeric characters are allowed"))

(defn passwords-match [first second]
  (when (not= first second) "Passwords must match"))

(defn email [val]
  (when-not (re-matches #"\S+@\S+.\S+"
                        val) "This is invalid email address"))

(defn length
  ([min val]
   (when (> min (count val)) (str "Please enter at least " min " characters")))
  ([min max val]
   (let [c (count val)]
     (when (or (> min c)
               (< max c)) (if (= min 0)
                            (str "Please enter string up to " max " characters")
                            (str "Please enter string between " min " and " max " characters"))))))


(defn user-bio [val]
  (length 0 140 val))

(defn password [val]
  (length 6 val))