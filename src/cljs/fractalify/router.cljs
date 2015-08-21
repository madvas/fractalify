(ns fractalify.router
  (:require-macros [secretary.core :refer [defroute]])
  (:require [schema.core :as s :include-macros true]
            [fractalify.history :as history]))

(def ^:dynamic *routes*
  (atom {}))

(defmulti panels identity)

(s/defn add-route!
  [route-name :- s/Keyword
   routeFn :- s/Any]
  (swap! *routes* assoc route-name routeFn))

(s/defn url :- s/Str
  ([route-name] (url route-name nil))
  ([route-name :- s/Keyword
    params :- (s/maybe {s/Keyword (s/either s/Int s/Str)})]
    ((route-name @*routes*) params)))

(defn go!
  [& args]
  (history/nav! (apply url args)))
