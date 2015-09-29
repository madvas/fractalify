(ns fractalify.api.users-tests
  (:require
    [fractalify.test-utils :as tu])
  (:use midje.sweet))


(tu/init-test-system)

(with-state-changes
  [(before :facts (tu/start-system))
   (after :facts (tu/stop-system))]

  (fact "it normally returns the first element"
        (conj [1 2] 3) => [1 2 3])

  (fact "asdas"
        (conj [1 2] 4) => [1 2 4]))
