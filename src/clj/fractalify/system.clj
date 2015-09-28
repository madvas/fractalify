(ns fractalify.system
  "Components and their dependency relationships"
  (:refer-clojure :exclude (read))
  (:require
    [com.stuartsierra.component :as c]
    [fractalify.config :as cfg]
    [fractalify.api.main.routes :as mr]
    [fractalify.api.users.routes :as ur]
    [fractalify.api.fractals.routes :as fr]
    [fractalify.figwheel :as fig]
    [modular.http-kit :as mh]
    [fractalify.utils :as u]
    [fractalify.router :as t]
    [fractalify.less-watcher :as lw]
    [fractalify.mongodb :as mongo]
    [fractalify.api.users.users-db :as udb]
    [fractalify.api.fractals.fractals-db :as fdb]
    [fractalify.api.users.users-generator :as ug]
    [fractalify.api.fractals.fractals-generator :as fg]
    [fractalify.middlewares :as mw]
    [fractalify.mailer :as ml]))


(defn make [f config & ks]
  (apply f (map #(u/eval-map (get-in config (u/ensure-vec %)))
                ks)))

(defn http-listener-components
  [system config]
  (assoc system :http-listener (make mh/map->Webserver config :http-listener)))

(defn router-components
  [system config]
  (assoc system :router (t/new-router)
                :main-routes (mr/new-main-routes)
                :user-routes (ur/new-user-routes)
                :fractal-routes (fr/new-fractal-routes)
                :middlewares (mw/new-middlewares)))


(defn fig-component [system config]
  (assoc system :figwheel (make fig/new-figwheel config :figwheel)))

(defn mailer-component [system config]
  (assoc system :mailer (make ml/new-mailer config :mailer :site)))

(defn less-component [system config]
  (assoc system :less-watcher (lw/new-less-watcher)))

(defn db-components [system config]
  (-> system
      (assoc :db-server (make mongo/new-mongodb config :db-server))
      (assoc :users-db (udb/new-users-db))
      (assoc :fractals-db (fdb/new-fractals-db))))

(defn generator-components [system config]
  (-> system
      (assoc :users-generator (ug/new-users-generator))
      (assoc :fractals-generator (fg/new-fractals-generator))))

(defn new-system-map
  [config]
  (apply c/system-map
         (apply concat
                (-> {}
                    (http-listener-components config)
                    (router-components config)
                    (db-components config)
                    (mailer-component config)))))



(defn dev-system-map [system-map config]
  (-> system-map
      (fig-component config)
      (less-component config)
      (generator-components config)))

(defn new-dependency-map []
  {:http-listener [:router]
   :router        [:db-server :fractal-routes :user-routes :main-routes :middlewares :mailer]
   :middlewares   [:db-server]
   :users-db      [:db-server]
   :fractals-db   [:db-server]
   :db-server     []})


(defn dev-dependency-map []
  {:figwheel           []
   :less-watcher       []
   :users-generator    [:db-server :users-db]
   :fractals-generator [:db-server :fractals-db :users-generator]})

(defn new-production-system
  "Create the production system"
  ([opts]
   (-> (new-system-map (merge (cfg/config) opts))
       (c/system-using (new-dependency-map))))
  ([] (new-production-system {})))
