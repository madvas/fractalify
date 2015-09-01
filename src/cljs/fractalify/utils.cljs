(ns fractalify.utils
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [schema.core :as s :include-macros true]
            [cljs.core.async :refer [chan close! >! <!]]))

(defn e-val [event]
  (aget event "target" "value"))

(def dev? goog.DEBUG)

(defn mlog [& messages]
  (.apply (.-log js/console) js/console (clj->js messages)))

(defn mwarn [& messages]
  (.apply (.-warn js/console) js/console (clj->js messages)))

(defn merror [& messages]
  (.apply (.-error js/console) js/console (clj->js messages)))


(s/defn validate-until-error-fn
  ([fns] (validate-until-error-fn nil fns))
  ([val :- s/Any
    fns :- [s/Any]]
    (when-let [f (first fns)]
      (if-let [error (f val)]
        error
        #(validate-until-error-fn val (rest fns))))))

(s/defn set-timeout
  [f
   ms :- s/Int]
  (js/setTimeout f ms))

(def clear-timeout js/clearTimeout)

(defn timeout [ms]
  (let [c (chan)]
    (js/setTimeout (fn [] (close! c)) ms)
    c))

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
  [num :- (s/either s/Num s/Str)]
  (js/parseFloat num))

(def validate-until-error
  "This is trampoline function, which stops executing until one of fns returns truthy value
  when val is passed to that function. It is used primary for getting error messages for inputs.
  Example:
  (do-until nil [reqired alphanumeric])
  => This field is required
  (do-until \"_*_\" [reqired alphanumeric])
  => This field needs to be alphanumeric"
  (partial trampoline validate-until-error-fn))

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