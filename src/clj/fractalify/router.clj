(ns fractalify.router
  (:require
    [schema.core :as s]
    [modular.ring :refer (WebRequestHandler)]
    [com.stuartsierra.component :as c]
    [bidi.bidi :as bidi :refer (match-route resolve-handler RouteProvider tag)]
    [bidi.bidi :as b]
    [bidi.ring :as br]
    [plumbing.core :as p]))

(defn dispatch-route [db resource]
  (println "resolved: " (type resource))
  (fn [res]
    ((resource db (:params res)) res)))

(defn as-request-handler
  "Convert a RouteProvider component into Ring handler."
  [service handler-fn]
  (assert (satisfies? RouteProvider service))
  (some-fn
    (br/make-handler
      (cond
        (satisfies? RouteProvider service)
        (b/routes service)) handler-fn)))

(defrecord Router [not-found-handler]
  c/Lifecycle
  (start [component]
    (assoc component
      :router ["" (vec
                    (remove nil?
                            (for [[ckey v] component]
                              (when (satisfies? RouteProvider v)
                                (bidi/routes v)))))]))
  (stop [this] (dissoc this :router))

  RouteProvider
  (routes [this] (:router this))

  WebRequestHandler
  (request-handler [this]
    (p/letk [[db] (:db-server this)
             [middlewares] (:middlewares this)]
      (middlewares (as-request-handler this (partial dispatch-route db))))))

(defn new-router []
  (map->Router {}))