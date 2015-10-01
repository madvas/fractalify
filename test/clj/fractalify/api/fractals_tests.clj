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

(defn fractal-path-for [route fractal]
  (path-for route :id (:id fractal)))

(defn get-fractals
  ([] (get-fractals {}))
  ([query]
   (a/get (path-for fr/fractals) {:query-params query})))

(def fractal-list?
  (every-checker a/status-ok
                 (a/response-schema fch/PublishedFractalsList)))

(def fractal?
  (every-checker a/status-ok
                 (a/response-schema fch/PublishedFractal)))

(defn get-fractal [fractal]
  (a/get (fractal-path-for fr/fractal fractal)))

(defn get-some-fractal []
  (p/letk [[[:body items]] (get-fractals)]
    (get-fractal (rand-nth items))))

(a/init-test-system)
(with-state-changes
  [(before :facts (a/start-system))
   (after :facts (a/stop-system))]

  (fact "gets list of fractals"
        (get-fractals) => fractal-list?)

  (fact "gets one fractal by id"
        (get-some-fractal) => fractal?)

  (future-fact)
  )







