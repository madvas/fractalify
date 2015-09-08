(ns fractalify.middleware
  (:require [fractalify.db :as db]
            [re-frame.core :as r :refer [after trim-v debug undoable]]
            [fractalify.utils :as u]))

(def enabled?
  ;true
  false
  )


(defn valid-schema? [db]
  (db/valid? db))

(defn print-db [handler]
  (fn [db v]
    (println db)
    (handler db v)))

(def trim-validate [trim-v
                    (when ^boolean goog.DEBUG (after valid-schema?))])

(def standard-middlewares [(when ^boolean (and goog.DEBUG enabled?) debug)
                           trim-validate])

(def standard-no-debug (rest standard-middlewares))