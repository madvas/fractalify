(ns workers.core
  (:import goog.events.EventHandler)
  (:require [goog.events :as e]
            [goog.events.EventType :as t]))

(defn evt-data [evt]
  (js->clj (aget (.-event_ evt) "data") :keywordize-keys true))

(defn on-message
  ([f] (on-message f js/self))
  ([f worker]
   (e/listen worker t/MESSAGE #(f (evt-data %)))))

(def msg-clb
  (fn [f e]
    (f (evt-data e))
    (.removeEventListener "message" msg-clb)))

(defn on-message-once
  ([f] (on-message f js/self))
  ([f worker]
   (let [event-handler (new goog.events.EventHandler)]
     (.listenOnce event-handler worker t/MESSAGE #(f (evt-data %))))))

(defn post-message
  ([data] (post-message data js/self))
  ([data worker] (.postMessage worker (clj->js data))))

