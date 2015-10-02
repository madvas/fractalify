(ns fractalify.router
  (:require
    [schema.core :as s]
    [modular.ring :as mr]
    [com.stuartsierra.component :as c]
    [bidi.bidi :as b]
    [bidi.ring :as br]
    [plumbing.core :as p]
    [fractalify.api.main.resources]
    [fractalify.api.api :as a]
    [fractalify.utils :as u]))

(s/defn match-route->resource
  [route :- s/Keyword
   route-providers :- [(s/protocol a/RouteResource)]]
  (let [routes-map (->> route-providers
                        (map a/route->resource)
                        (reduce merge {}))]
    (route routes-map)))

(defn satisfies-route? [x]
  (and (satisfies? b/RouteProvider x)
       (satisfies? a/RouteResource x)))

(defn get-route-providers [router]
  (->> router
       (map val)
       (filter satisfies-route?)))

(s/defn dispatch-route
  [router db mailer matched-route :- s/Keyword]
  (let [resource (match-route->resource matched-route (get-route-providers router))]
    (fn [res]
      ((resource {:db     db
                  :params (:params res)
                  :mailer mailer}) res))))

(s/defn as-request-handler
  "Convert a RouteProvider component into Ring handler."
  [service :- (s/protocol b/RouteProvider) handler-fn]
  (some-fn
    (br/make-handler
      (cond
        (satisfies? b/RouteProvider service)
        (b/routes service)) handler-fn)))

(defrecord Router []
  c/Lifecycle
  (start [this]
    (assoc this
      :router ["" (->> (get-route-providers this)
                       (map b/routes)
                       (remove nil?)
                       vec)]))
  (stop [this]
    (dissoc this :router))

  b/RouteProvider
  (routes [this] (:router this))

  mr/WebRequestHandler
  (request-handler [this]
    (p/letk [[db] (:db-server this)
             [mailer] this
             [middlewares] (:middlewares this)]
      (middlewares (as-request-handler this (partial dispatch-route this db mailer))))))

(defn new-router []
  (map->Router {}))