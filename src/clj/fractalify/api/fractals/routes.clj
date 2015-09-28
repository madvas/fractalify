(ns fractalify.api.fractals.routes
  (:require
    [bidi.bidi :as b]
    [liberator.core :refer [defresource]]
    [fractalify.utils :as u]
    [fractalify.api.fractals.fractals-db :as fdb]
    [fractalify.api.api :as api]
    [schema.core :as s]
    [fractalify.fractals.schemas :as fch]))

(defresource
  fractal [{:keys [db params]}]
  api/base-resource
  :exists?
  (fn [_]
    (when-let [fractal (fdb/get-fractal db (:id params))]
      {::fractal fractal}))
  :handle-ok ::fractal)

(defresource
  fractals [{:keys [db params]}]
  api/base-resource
  :handle-ok (s/fn :- fch/PublishedFractalsList [_]
               {:total-items (fdb/fractal-count db)
                :items       (fdb/get-fractals db params)}))

(def routes
  ["/api/fractals" {["/" :id] fractal
                    ""        fractals}])

(defrecord FractalRoutes []
  b/RouteProvider
  (routes [_]
    routes))

(defn new-fractal-routes []
  (->FractalRoutes))
