(ns fractalify.utils
  (:require [schema.core :as s :include-macros true]
            [cljs.core.async :refer [chan close!]]))

(defn e-val [event]
  (-> event .-target .-value))

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

(defn set-timeout [f ms]
  (js/setTimeout f ms))

(defn timeout [ms]
  (let [c (chan)]
    (js/setTimeout (fn [] (close! c)) ms)
    c))

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