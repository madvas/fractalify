(ns fractalify.utils
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [schema.core :as s :include-macros true]
            [cljs.core.async :refer [chan close! >! <!]]
            [cljs.core]
            [instar.core :as i]
            [clojure.string :as str]))

(defn p [& args]
  "Like println, but returns last arg. For debugging purposes"
  (apply cljs.core/println args)
  (last args))

(defn mlog [& messages]
  (.apply (.-log js/console) js/console (clj->js messages)))

(defn mwarn [& messages]
  (.apply (.-warn js/console) js/console (clj->js messages)))

(defn merror [& messages]
  (.apply (.-error js/console) js/console (clj->js messages)))

(defn e-val [event]
  (aget event "target" "value"))

(defn range-count [coll]
  (range 0 (count coll)))

(def dev? goog.DEBUG)

(def select-values (comp vals select-keys))
(s/defn set-timeout
  [f
   ms :- s/Int]
  (js/setTimeout f ms))

(def clear-timeout js/clearTimeout)

(defn timeout [ms]
  (let [c (chan)]
    (js/setTimeout (fn [] (close! c)) ms)
    c))

(defn concat-vec
  [& args]
  (vec (apply concat args)))

(s/defn round
  "Round a double to the given precision (number of significant digits)"
  [d :- s/Num
   precision :- s/Int]
  (let [factor (Math/pow 10 precision)]
    (/ (Math/round (* d factor)) factor)))

(def deg (/ (.-PI js/Math) 180))

(defn debounce
  ([c ms] (debounce (chan) c ms false))
  ([c ms instant] (debounce (chan) c ms instant))
  ([c' c ms instant]
   (go
     (loop [start (js/Date.) timeout nil]
       (let [loc (<! c)]
         (when timeout
           (js/clearTimeout timeout))
         (let [diff (- (js/Date.) start)
               delay (if (and instant
                              (or (>= diff ms)
                                  (not timeout)))
                       0 ms)
               t (js/setTimeout #(go (>! c' loc)) delay)]
           (recur (js/Date.) t)))))
   c'))

(defn throttle [c ms]
  (let [c' (chan)]
    (go
      (while true
        (>! c' (<! c))
        (<! (timeout ms))))
    c'))

(s/defn parse-float :- (s/maybe s/Num)
  [num :- (s/cond-pre s/Num s/Str)]
  (js/parseFloat num))

(defn parse-int [num]
  (p "parsing int:" (js/parseInt num)))

#_ (s/defn parse-int :- (s/maybe s/Num)
  [num :- (s/cond-pre s/Num s/Str)]
  (p "parsing int:" (js/parseInt num)))

(s/defn validate-until-error-fn
  ([fns] (validate-until-error-fn nil fns))
  ([val :- s/Any
    fns :- [s/Any]]
    (when-let [f (first fns)]
      (if-let [error (f val)]
        error
        #(validate-until-error-fn val (rest fns))))))

(def validate-until-error
  "This is trampoline function, which stops executing until one of fns returns truthy value
  when val is passed to that function. It is used primary for getting error messages for inputs.
  Example:
  (do-until nil [reqired alphanumeric])
  => This field is required
  (do-until \"_*_\" [reqired alphanumeric])
  => This field needs to be alphanumeric"
  (partial trampoline validate-until-error-fn))

(defn drop-nth [n coll]
  (lazy-seq
    (when-let [s (seq coll)]
      (concat (take (dec n) s) (drop-nth n (drop n s))))))

(defn vec-remove
  "remove elem in coll"
  [coll pos]
  (vec (concat (subvec coll 0 pos) (subvec coll (inc pos)))))

(defn partial-right
  "Takes a function f and fewer than the normal arguments to f, and
 returns a fn that takes a variable number of additional args. When
 called, the returned function calls f with additional args + args."
  ([f] f)
  ([f arg1]
   (fn [& args] (apply f (concat args [arg1]))))
  ([f arg1 arg2]
   (fn [& args] (apply f (concat args [arg1 arg2]))))
  ([f arg1 arg2 arg3]
   (fn [& args] (apply f (concat args [arg1 arg2 arg3]))))
  ([f arg1 arg2 arg3 & more]
   (fn [& args] (apply f (concat args (concat [arg1 arg2 arg3] more))))))

(def trim-base64-prefix (partial-right str/replace-first #"^data:image/\w+;base64," ""))

(defn dissoc-in
  "Dissociates an entry from a nested associative structure returning a new
  nested structure. keys is a sequence of keys. Any empty maps that result
  will not be present in the new structure."
  [m [k & ks :as keys]]
  (if ks
    (if-let [nextmap (get m k)]
      (let [newmap (dissoc-in nextmap ks)]
        (if (seq newmap)
          (assoc m k newmap)
          (dissoc m k)))
      m)
    (dissoc m k)))

(defn rand-str [chars n]
  (->> #(rand-nth (vec chars))
       (repeatedly n)
       (apply str)))

(defn char-range [start end]
  (map char (range (int start) (inc (int end)))))

(defn rand-id [n]
  (rand-str "ABCDEF" n))
