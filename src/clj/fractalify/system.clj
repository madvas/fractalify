(ns fractalify.system
  "Components and their dependency relationships"
  (:refer-clojure :exclude (read))
  (:require
    [fractalify.readers]
    [com.stuartsierra.component :as c]
    [fractalify.config :as cfg]
    [fractalify.api.main.resources :as mr]
    [fractalify.api.users.resources :as ur]
    [fractalify.api.fractals.resources :as fr]
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
    [fractalify.mailers.sendgrid :as sgm]
    [fractalify.mailers.mailer :as mm]
    [fractalify.img-cloud.cloudinary :as cloudinary]))


(defn http-listener-components
  [system config]
  (assoc system :http-listener (mh/map->Webserver (:http-listener config))))

(defn router-components
  [system config]
  (assoc system :router (t/new-router (:site config))
                :main-routes (mr/new-main-routes)
                :user-routes (ur/new-user-routes)
                :fractal-routes (fr/new-fractal-routes)
                :middlewares (mw/new-middlewares)))


(defn fig-component [system config]
  (assoc system :figwheel (fig/new-figwheel (:figwheel config))))

(defn mailer-components [system config]
  (-> system
      (assoc :mailer (mm/new-mailer (:mailer config) (:site config)))
      (assoc :mail-sender (sgm/new-sendgrid-mail-sender (:sendgrid-mail-sender config)))))

(defn less-component [system config]
  (assoc system :less-watcher (lw/new-less-watcher)))

(defn db-components [system config]
  (-> system
      (assoc :db-server (mongo/new-mongodb (:db-server config)))
      (assoc :users-db (udb/new-users-db))
      (assoc :fractals-db (fdb/new-fractals-db))))

(defn generator-components [system config]
  (-> system
      (assoc :users-generator (ug/new-users-generator))
      (assoc :fractals-generator (fg/new-fractals-generator))))

(defn img-cloud-component [system config]
  (assoc system :img-cloud (cloudinary/new-cloudinary (:cloudinary config))))

(defn new-system-map
  [config]
  (apply c/system-map
         (apply concat
                (-> {}
                    (http-listener-components config)
                    (router-components config)
                    (db-components config)
                    (mailer-components config)
                    (img-cloud-component config)))))


(defn dev-system-map [system-map config]
  (-> system-map
      (fig-component config)
      (less-component config)
      (generator-components config)))

(defn new-dependency-map []
  {:http-listener [:router]
   :router        [:db-server :fractal-routes :user-routes :main-routes :middlewares :mailer :img-cloud]
   :users-db      [:db-server]
   :fractals-db   [:db-server]
   :mailer        [:mail-sender]})


(def db-generators-dependencies
  {:users-generator    [:db-server :users-db]
   :fractals-generator [:db-server :fractals-db :users-generator]})

(defn dev-dependency-map []
  db-generators-dependencies)

(defn new-production-system
  "Create the production system"
  ([] (new-production-system {}))
  ([opts]
   (-> (new-system-map (merge (cfg/config) opts))
       (c/system-using (new-dependency-map)))))
