(ns fractalify.tracer
  (:require
    [re-frame-tracer.core :as t]
    [clairvoyant.core :as c :refer [ITraceEnter ITraceError ITraceExit]]
    [fractalify.utils :as u]))

(def always-trace
  true
  ;false
  )

(def trace? #(or (.-TRACE js/window) always-trace))

(defn tracer
  [& args]
  (let [tr (apply t/tracer args)]
    (reify
      ITraceEnter
      (-trace-enter
        [_ trace-data]
        (when (trace?)
          (c/trace-enter tr trace-data)))

      ITraceExit
      (-trace-exit
        [_ trace-data]
        (when (trace?)
          (c/trace-exit tr trace-data)))

      ITraceError
      (-trace-error
        [_ trace-data]
        (when (trace?)
          (c/trace-error tr trace-data))))))
