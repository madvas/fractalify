(ns fractalify.router
  (:require
    [schema.core :as s]
    [modular.ring :as mr]
    [com.stuartsierra.component :as c]
    [bidi.bidi :as b]
    [bidi.ring :as br]
    [plumbing.core :as p]
    [fractalify.api.main.routes]))

(defn dispatch-route [db mailer resource]
#_   (println "resolved: " (type resource))
  (fn [res]
    ((resource {:db     db
                :params (:params res)
                :mailer mailer}) res)))

(defn as-request-handler
  "Convert a RouteProvider component into Ring handler."
  [service handler-fn]
  (assert (satisfies? b/RouteProvider service))
  (some-fn
    (br/make-handler
      (cond
        (satisfies? b/RouteProvider service)
        (b/routes service)) handler-fn)))

(defrecord Router []
  c/Lifecycle
  (start [component]
    (assoc component
      :router ["" (vec
                    (remove nil?
                            (for [[ckey v] component]
                              (when (satisfies? b/RouteProvider v)
                                (b/routes v)))))]))
  (stop [this]
    (dissoc this :router))

  b/RouteProvider
  (routes [this] (:router this))

  mr/WebRequestHandler
  (request-handler [this]
    (p/letk [[db] (:db-server this)
             [mailer] this
             [middlewares] (:middlewares this)]
      (middlewares (as-request-handler this (partial dispatch-route db mailer))))))

(defn new-router []
  (map->Router {}))