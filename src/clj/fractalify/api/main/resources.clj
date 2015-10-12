(ns fractalify.api.main.resources
  (:require
    [net.cgrand.enlive-html :refer [deftemplate]]
    [net.cgrand.enlive-html :refer [set-attr prepend append html]]
    [bidi.bidi :refer (path-for RouteProvider)]
    [liberator.core :as l :refer [defresource]]
    [fractalify.utils :as u]
    [clojure.string :as str]
    [clojure.java.io :as io]
    [fractalify.api.api :as a]
    [fractalify.main.api-routes :as mar]
    [io.clojure.liberator-transit]
    [ring.middleware.resource :refer [wrap-resource]]
    [ring.middleware.content-type :refer [wrap-content-type]]
    [ring.middleware.not-modified :refer [wrap-not-modified]]
    [fractalify.main.schemas :as mch]
    [fractalify.mailers.mailer :as mm]))

(def inject-devmode-html
  (comp
    (set-attr :class "is-dev")
    (prepend (html [:script {:type "text/javascript" :src "/public/js/out/goog/base.js"}]))
    (append (html [:script {:type "text/javascript"} "goog.require('fractalify.main')"]))))

(def static-dir "resources")

(deftemplate
  page (io/resource "index.html") []
  [:body] (if u/is-dev? inject-devmode-html identity))

(def page-html
  (str/join "" (page)))

(defresource
  main [_]
  :available-media-types ["text/html" "text/plain"]
  :handle-ok page-html)

(defresource
  contact [{:keys [params mailer site]}]
  a/base-post
  :malformed? (a/malformed-params? mch/ContactForm params)
  :post-redirect? false
  :post!
  (fn [_]
    (mm/send-email
      mailer
      {:subject (:subject params)
       :text    (str "From: " (:email params) "\n" (:text params))
       :to      (:contact-email site)})))

(defn static [_]
  (-> {}
      (wrap-resource "")
      (wrap-content-type)
      (wrap-not-modified)))

(def routes->resources
  {:static  static
   :contact contact
   :main    main})

(defrecord MainRoutes []
  RouteProvider
  (routes [_]
    (mar/get-routes))

  a/RouteResource
  (route->resource [_]
    routes->resources))

(defn new-main-routes []
  (->MainRoutes))
