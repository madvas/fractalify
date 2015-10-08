(ns fractalify.middlewares
  (:require
    [com.stuartsierra.component :as c]
    [ring.middleware.reload :as reload]
    [clojure.pprint :refer [pprint]]
    [plumbing.core :as p]
    [ring.middleware.session :as session]
    [ring.middleware.params :as params]
    [ring.middleware.nested-params :as nested-params]
    [ring.middleware.defaults :as defaults]
    [cemerick.drawbridge :as drawbridge]
    [ring.middleware.keyword-params :as keyword-params]
    [ring.middleware.basic-authentication :refer [wrap-basic-authentication]]
    [ring.middleware.edn :refer [wrap-edn-params]]
    [ring.middleware.format :refer [wrap-restful-format]]
    [fractalify.utils :as u]
    [fractalify.api.users.resources :as ur]
    [liberator.dev :as ld]
    [cemerick.friend :as frd]
    (cemerick.friend [workflows :as workflows])
    [fractalify.api.users.users-db :as udb]))


(defn authenticated? [name pass]
  (= [name pass] [(System/getenv "AUTH_USER") (System/getenv "AUTH_PASS")]))

(def drawbridge-handler
  (-> (drawbridge/ring-handler)
      (keyword-params/wrap-keyword-params)
      (nested-params/wrap-nested-params)
      (params/wrap-params)
      (session/wrap-session)))

(defn debug-handler [handler]
  (fn [req]
    (pprint req)
    (handler req)))

(def ring-defaults (-> defaults/site-defaults
                       (u/dissoc-in [:security :anti-forgery])))

(defn get-middlewares [handler]
  (let [middewares (-> handler
                       (frd/authenticate nil)
                       (p/?> u/is-dev? (ld/wrap-trace :header :ui))
                       (p/?> u/is-dev? reload/wrap-reload)
                       (wrap-restful-format :formats [:transit-json])
                       #_ debug-handler
                       (defaults/wrap-defaults ring-defaults))]
    (fn [req]
      (let [handler (condp = (:uri req)
                      "/repl" (wrap-basic-authentication drawbridge-handler authenticated?)
                      ur/login-url middewares
                      middewares)]
        (handler req)))))

(defrecord Middlewares []
  c/Lifecycle
  (start [this]
    (p/letk [[db] (:db-server this)]
      (assoc this :middlewares get-middlewares)))

  (stop [this] (dissoc this :middlewares)))

(defn new-middlewares []
  (map->Middlewares {}))