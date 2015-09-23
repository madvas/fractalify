(ns fractalify.router
  (:require
    [schema.core :as s]
    [modular.ring :refer (WebRequestHandler)]
    [com.stuartsierra.component :as component :refer (Lifecycle)]
    [bidi.bidi :as bidi :refer (match-route resolve-handler RouteProvider tag)]
    [bidi.ring :refer (resources-maybe make-handler redirect archive)]
    [bidi.bidi :as b]
    [bidi.ring :as br]
    [ring.middleware.reload :as reload]
    [plumbing.core :as p]
    [ring.middleware.session :as session]
    [ring.middleware.params :as params]
    [ring.middleware.nested-params :as nested-params]
    [ring.middleware.defaults :as defaults]
    [cemerick.drawbridge :as drawbridge]
    [ring.middleware.keyword-params :as keyword-params]
    [ring.middleware.basic-authentication :refer [wrap-basic-authentication]]
    [ring.middleware.edn :refer [wrap-edn-params]]
    [fractalify.utils :as u]
    [liberator.dev :as ld]))

(defn dispatch-route [db resource]
  (fn [res]
    ((resource db (:params res)) res)))

(defn authenticated? [name pass]
  (= [name pass] [(System/getenv "AUTH_USER") (System/getenv "AUTH_PASS")]))

(def drawbridge-handler
  (-> (drawbridge/ring-handler)
      (keyword-params/wrap-keyword-params)
      (nested-params/wrap-nested-params)
      (params/wrap-params)
      (session/wrap-session)))

(defn http-handler [handler]
  (-> handler
      (defaults/wrap-defaults defaults/site-defaults)
      wrap-edn-params))

(defn wrap-http [handler]
  (fn [req]
    (let [handler (if (= "/repl" (:uri req))
                    (wrap-basic-authentication drawbridge-handler authenticated?)
                    (-> handler
                        http-handler
                        (p/?> u/is-dev? reload/wrap-reload)
                        (p/?> u/is-dev? (ld/wrap-trace :header :ui))))]
      (handler req))))

(defn as-request-handler
  "Convert a RouteProvider component into Ring handler."
  [service handler-fn not-found-handler]
  (assert (satisfies? RouteProvider service))
  (some-fn
    (br/make-handler
      (cond
        (satisfies? RouteProvider service)
        (b/routes service)) handler-fn)
    not-found-handler))

(def new-router-schema
  {:not-found-handler (s/=>* {:status   s/Int
                              s/Keyword s/Any}
                             [{:uri      s/Str
                               s/Keyword s/Any}])})

(defrecord Router [not-found-handler]
  component/Lifecycle
  (start [component]
    (assoc component
      :router ["" (vec
                    (remove nil?
                            (for [[ckey v] component]
                              (when (satisfies? RouteProvider v)
                                (bidi/routes v)))))]))
  (stop [this] this)

  RouteProvider
  (routes [this] (:router this))

  WebRequestHandler
  (request-handler [this]
    (p/letk [[db] (:db-server this)]
      (wrap-http (as-request-handler this (partial dispatch-route db) nil)))))

(defn new-router
  "Constructor for a ring handler that collates all bidi routes
  provided by its dependencies."
  [& {:as opts}]
  (->> opts
       (merge {:not-found-handler (constantly {:status 404 :body "Not found"})})
       (s/validate new-router-schema)
       map->Router))