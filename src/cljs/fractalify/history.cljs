(ns fractalify.history
  (:import goog.History)
  (:require [goog.events :as events]
            [goog.history.EventType :as EventType]
            [re-frame.core :as r]
            [fractalify.main.components.sidenav :as sidenav]
            [secretary.core :as secretary]
            [fractalify.utils :as u]))

(def ^:dynamic *history* nil)

(defn setup! []
  (set! *history* (doto (History.)
                    (events/listen
                      EventType/NAVIGATE
                      (fn [event]
                        (sidenav/close-sidenav!)
                        (secretary/dispatch! (.-token event))))
                    (.setEnabled true))))

(defn nav! [token]
  (.setToken *history* token))