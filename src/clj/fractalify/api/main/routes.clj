(ns fractalify.api.main.routes
  (:require
    [net.cgrand.enlive-html :refer [deftemplate]]
    [net.cgrand.enlive-html :refer [set-attr prepend append html]]
    [bidi.bidi :refer (path-for RouteProvider)]
    [ring.util.mime-type :as mime]
    [liberator.core :as l :refer [defresource]]
    [fractalify.utils :as u]
    [clojure.string :as str]
    [clojure.java.io :as io]))

(def inject-devmode-html
  (comp
    (set-attr :class "is-dev")
    (prepend (html [:script {:type "text/javascript" :src "/public/js/out/goog/base.js"}]))
    (append (html [:script {:type "text/javascript"} "goog.require('fractalify.main')"]))))

(def static-dir "resources")

(deftemplate page (io/resource "index.html") []
             [:body] (if u/is-dev? inject-devmode-html identity))


(defresource static-routes [db params]
             :available-media-types
             (fn [ctx]
               (let [file (get-in ctx [:request :uri])]
                 (if-let [mime-type (mime/ext-mime-type file)]
                   [mime-type]
                   [])))

             :exists?
             (fn [ctx]
               (let [file (-> (get-in ctx [:request :uri])
                              (str/replace-first "/" "")
                              io/resource
                              io/file)]
                 (if file
                   [(.exists file) {::file file}]
                   false)))

             :handle-ok ::file

             :last-modified
             (fn [ctx]
               (.lastModified (::file ctx))))



(defresource main-routes [db params]
             :available-media-types ["text/html" "text/plain"]
             :handle-ok (str/join "" (page)))

(def routes
  ["/" [["public/" [[true static-routes]]]
        [true main-routes]
        ]])

(defrecord MainRoutes []
  RouteProvider
  (routes [_]
    routes))

(defn new-main-routes []
  (->MainRoutes))