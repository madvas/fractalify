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

(p/defnk dispatch-route [handler {tag nil} {route-params nil}]
  (let [active-panel (if (keyword? handler) handler tag)]
    (f/dispatch [:set-active-panel active-panel route-params])))

(defn add-routes! [routes]
  (swap! *routes* (fn [[root current] new]
                    [root (merge current new)])
         routes))

(defn url [& args]
  (apply b/path-for @*routes* args))

(defn start! []
  (reset! *history* (pu/pushy dispatch-route parse-url))
  (pu/start! @*history*))

(defn go! [& route]
  (pu/set-token! @*history* (apply url route)))

(defn replace! [& route]
  (pu/replace-token! @*history* (apply url route)))
