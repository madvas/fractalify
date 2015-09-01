(ns workers.core
  (:import goog.events.EventHandler)
  (:require [goog.events.EventHandler :as h]
            [goog.events.EventType :as t]))

(defn evt-data [evt]
  (js->clj (aget evt "data") :keywordize-keys true))

(defn on-message
  ([f] (on-message f js/self))
  ([f worker]
   (.addEventListener worker "message" #(f (evt-data %)) false)))

(def msg-clb
  (fn [f e]
    (f (evt-data e))
    (.removeEventListener "message" msg-clb)))

(defn on-message-once
  ([f] (on-message f js/self))
  ([f worker]
   (println (new goog.events.EventHandler))
    (.listenOnce (new goog.events.EventHandler) worker t/MESSAGE)))

(defn post-message
  ([data] (post-message data js/self))
  ([data worker] (.postMessage worker (clj->js data))))

