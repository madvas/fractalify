(ns fractalify.api.fractals-tests
  (:require
    [fractalify.api :as a]
    [fractalify.fractals.schemas :as fch]
    [fractalify.utils :as u]
    [fractalify.api.fractals.routes :as fr]
    [plumbing.core :as p]
    [bidi.bidi :as b])
  (:use midje.sweet))


(def path-for (partial b/path-for fr/routes))

(defn get-fractals
  ([] (get-fractals {}))
  ([query]
   (a/get (path-for fr/fractals) {:query-params query})))

(a/init-test-system)
(with-state-changes
  [(before :facts (a/start-system))
   (after :facts (a/stop-system))]

  (fact "gets list of fractals"
        (get-fractals) => (every-checker a/status-ok
                                         (a/response-schema fch/PublishedFractalsList))))







