(ns fractalify.middleware
  (:require [fractalify.db :as db]
            [re-frame.core :as r :refer [after trim-v debug undoable]]
            [fractalify.utils :as u]))

(def enabled?
  ;true
  false
  )

(def standard-middlewares [(when ^boolean (and goog.DEBUG enabled?) debug)
                           trim-v
                           (when ^boolean goog.DEBUG (after db/valid?))])

(def standard-no-debug (rest standard-middlewares))