(ns fractalify.fractals.lib.parallel
  (:require [fractalify.utils :as u]))

(defn spawn
  "Wraps Parallel.js spawn function.  Takes a function to run in
    parallel, p-func.  This function must take one argument, the
    data which is also passed.  Optionally, a callback function (cb-func)
    of one argument, the data returned, can be passed to handle any
    return data from the web worker"
  ([p-func data] (let [p (new js/Parallel (clj->js data))]
                   (.spawn p p-func)))
  ([p-func data cb-func] (let [p (new js/Parallel (clj->js data))]
                           (.then
                             (.spawn p p-func) cb-func))))

;; example use

(defn ultra-fn []
  (.log js/console "something"))

(let [p (new js/Parallel "forwards")
      my-reverse (.-log js/console)]
  (u/mlog (.-data p))
  (.require p my-reverse)
  (u/mlog "after req")
  (.then
    (.spawn p
            (fn [data]
              (.log js/console "spawn callback")
              (.log js/console "whattty")
              (.log js/console (my-reverse))
              data))
    (fn [data]
      (u/mlog data)
      data)))

(defn process-data [data]
  (map #(* % %) data))

(defn handle-data [data]
  (.log js/console "heree")
  (doseq [d data]
    (.log js/console d)))

(defn data-clb []
  (.log js/console "callback"))

;; should run in web worker, but I haven't tested
(spawn process-data (range 1 100) handle-data)
(.log js/console "This should print first")
;;if everything worked, numbers will print last