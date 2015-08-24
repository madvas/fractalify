(ns fractalify.middleware
  (:require [fractalify.db :as db]
            [re-frame.core :as r :refer [after trim-v debug]]
            [fractalify.utils :as u]))

(defn valid-schema? [db]
  (db/valid? db))

(defn print-db [handler]
  (fn [db v]
    (println db)
    (handler db v)))

(def trim-validate [trim-v
                    (when ^boolean goog.DEBUG (after valid-schema?))])

(def standard-middlewares [(when ^boolean (and goog.DEBUG false) debug)
                           trim-validate])

(def standard-without-debug (rest standard-middlewares))