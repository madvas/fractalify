(ns fractalify.router
  (:require [clojure.set :refer [rename-keys]]
            [bidi.bidi :as b]
            [pushy.core :as pu]
            [re-frame.core :as f]
            [fractalify.utils :as u]
            [plumbing.core :as p]))

(defmulti panels identity)

(def ^:dynamic *routes* (atom ["/" {}]))
(def ^:dynamic *history* (atom))

(declare go!)
(declare swap-route!)

(defn- parse-url [url]
  (b/match-route @*routes* url))

(p/defnk dispatch-route [handler {tag nil} {permissions nil} {route-params nil}]
  (println "route:" handler route-params)
  (let [active-panel (if (keyword? handler) handler tag)]
    #_ (reset! *current-params* route-params)
    (f/dispatch [:set-active-panel active-panel permissions route-params])))

(defn add-routes! [routes]
  (swap! *routes* (fn [[root current] new]
                    [root (merge current new)])
         routes))

(defrecord PermissionMatch [matched permissions]
  b/Matched
  (resolve-handler [_ m]
    (b/resolve-handler matched (assoc m :permissions permissions)))
  (unresolve-handler [_ m]
    (b/unresolve-handler matched m)))

(def perm ->PermissionMatch)

(defn url [& args]
  (apply b/path-for @*routes* args))

(defn start! []
  (reset! *history* (pu/pushy dispatch-route parse-url))
  (pu/start! @*history*))

(defn go! [& route]
  (pu/set-token! @*history* (apply url route)))

(defn replace! [& route]
  (pu/replace-token! @*history* (apply url route)))
