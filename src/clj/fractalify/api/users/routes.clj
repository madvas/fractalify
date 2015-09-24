(ns fractalify.api.users.routes
  (:require
    [modular.ring :refer (WebRequestHandler)]
    [bidi.bidi :refer (path-for RouteProvider)]
    [liberator.core :refer [defresource]]
    [fractalify.utils :as u]
    [fractalify.api.api :as api]))

(def base-url "/api/users")
(def login-url (str base-url "/login"))

(defresource user-res [db params]
             api/base-resource
             :handle-ok (fn [_] (format "The text is %s" (:username params))))

(defresource user-list-res [db params]
             api/base-resource
             :handle-ok (fn [_] {:a 2}))

(def routes
  [base-url {["/" :username] user-res
             ""              user-list-res}])



(defrecord UserRoutes []
  RouteProvider
  (routes [_]
    routes))

(defn new-user-routes []
  (->UserRoutes))
