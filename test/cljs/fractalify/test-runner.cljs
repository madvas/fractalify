(ns fractalify.test-runner
  (:require
   [cljs.test :refer-macros [run-tests]]
   [fractalify.core-test]))

(enable-console-print!)

(defn runner []
  (if (cljs.test/successful?
       (run-tests
        'fractalify.core-test))
    0
    1))
