(ns fractalify.api.fractals.routes
  (:require
    [bidi.bidi :as b]
    [liberator.core :refer [defresource]]
    [fractalify.utils :as u]
    [fractalify.api.fractals.fractals-db :as fdb]
    [fractalify.api.api :as api]
    [schema.core :as s]
    [fractalify.fractals.schemas :as fch]))

(defn fractal-exists-fn [db params]
  (fn [_]
    (when-let [fractal (fdb/fractal-get-by-id db (:id params))]
      {::fractal fractal})))

(defresource
  fractal [{:keys [db params]}]
  api/base-resource
  :allowed-methods [:get :delete :put]
  :exists? (fractal-exists-fn db params)
  :handle-ok
  (s/fn :- fch/PublishedFractal [ctx]
    (::fractal ctx)))

(defresource
  fractals [{:keys [db params]}]
  api/base-resource
  :malformed? (api/malformed-params? fch/FractalListForm params)
  :handle-ok
  (s/fn :- fch/PublishedFractalsList [_]
    {:total-items (fdb/fractal-count db)
     :items       (fdb/get-fractals db params)}))

(defresource
  fractal-star [{:keys [db params]}]
  api/base-resource
  :exists? (fractal-exists-fn db params)
  :handle-ok
  (s/fn :- fch/PublishedFractalsList [_]
    {:total-items (fdb/fractal-count db)
     :items       (fdb/get-fractals db params)}))

(def routes
  ["/api/fractals" {["/" :id] [["/star" fractal-star
                                "/comments" fractal-comments
                                "" fractal]]
                    ""        fractals}])

(defrecord FractalRoutes []
  b/RouteProvider
  (routes [_]
    routes))

(defn new-fractal-routes []
  (->FractalRoutes))
