(ns fractalify.api.users.routes
  (:require
    [modular.ring :refer (WebRequestHandler)]
    [bidi.bidi :refer (path-for RouteProvider)]
    [liberator.core :refer [defresource]]
    [fractalify.utils :as u]))

(defn get-fractals [params]
  )

(defresource user-res [db params]
             :available-media-types ["application/edn"]
             :handle-ok (fn [_] (format "The text is %s" (:username params))))

(defresource user-list-res [db params]
             :available-media-types ["application/edn"]
             :handle-ok (fn [_] {:a 2})
             )

(def routes
  ["/api/users" {["/" :username] user-res
                 ""              user-list-res}])

(defrecord UserRoutes []
  RouteProvider
  (routes [_]
    routes))

(defn new-user-routes []
  (->UserRoutes))
