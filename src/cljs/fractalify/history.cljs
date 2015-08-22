(ns fractalify.history
  (:import goog.History)
  (:require [goog.events :as events]
            [goog.history.EventType :as EventType]
            [re-frame.core :as r]
            [secretary.core :as secretary]
            [fractalify.utils :as u]
            [re-frame.core :as f]))

(def ^:dynamic *history* nil)

(defn setup! []
  (set! *history* (doto (History.)
                    (events/listen
                      EventType/NAVIGATE
                      (fn [event]
                        (f/dispatch [:sidenav-action :close])
                        (secretary/dispatch! (.-token event))))
                    (.setEnabled true))))

(defn nav! [token]
  (.setToken *history* token))