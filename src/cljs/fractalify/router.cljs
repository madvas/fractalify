(ns fractalify.router
  (:require [clojure.set :refer [rename-keys]]
            [bidi.bidi :as b]
            [pushy.core :as p]
            [re-frame.core :as f]
            [fractalify.utils :as u]))

(defmulti panels identity)

(def ^:dynamic *routes* (atom ["/" {}]))
(def ^:dynamic *history* (atom))

(declare go!)
(declare swap-route!)

(defn- parse-url [url]
  (b/match-route @*routes* url))

(defn- dispatch-route [matched-route]
  (println "route:" matched-route)
  (let [active-panel (:handler matched-route)
        active-panel (if (keyword? active-panel)
                       active-panel
                       (:tag matched-route))]
    (f/dispatch [:assoc-db :route-params (:route-params matched-route)])
    (f/dispatch [:set-active-panel active-panel (:permissions matched-route)])
    (f/dispatch [:sidenav-action :close])))

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
  (reset! *history* (p/pushy dispatch-route parse-url))
  (p/start! @*history*))

(defn go! [route]
  (p/set-token! @*history* (url route)))

(defn replace! [route]
  (p/replace-token! @*history* (url route)))
