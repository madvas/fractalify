(ns fractalify.api.fractals.routes
  (:require
    [bidi.bidi :as b]
    [liberator.core :refer [defresource]]
    [fractalify.utils :as u]
    [fractalify.api.fractals.fractals-db :as fdb]
    [fractalify.api.api :as api]))

(defresource fractal-res [db params]
             api/base-resource
             :handle-ok (fn [_] (format "The text is %s" (:id params))))

(defresource fractal-list-res [db params]
             api/base-resource
             :handle-ok (fn [_] (fdb/get-fractals db params)))

(def routes
  ["/api/fractals" {["/" :id] fractal-res
                    ""        fractal-list-res}])

(defrecord FractalRoutes []
  b/RouteProvider
  (routes [_]
    routes))

(defn new-fractal-routes []
  (->FractalRoutes))
