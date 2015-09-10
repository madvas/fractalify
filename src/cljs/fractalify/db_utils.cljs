(ns fractalify.db-utils
  (:require [fractalify.utils :as u]
            [re-frame.core :as f]
            [plumbing.core :as p]
            [cljs.core]))

(def logged-user (u/partial-right get-in [:users :logged-user]))

(defn get-form-data [db module form]
  (-> db
      (get-in [module :forms form])
      (dissoc :errors)))

(defn loading? [db-item]
  (:loading (meta db-item)))

(defn empty? [db-item]
  (cljs.core/empty? db-item))

(defn query
  [db path new-query-params required-active-panel]
  (p/letk [val (get-in db path)
           [{query-params nil} {loading nil} {error nil}] (or (meta val) {})]
    (println "query" path new-query-params)
    ;(u/p "old-q:" query-params)
    ;(u/p "new-q:" new-query-params)
    ;(u/p "loading:" loading)
    ;(u/p "error:" error)
    (when (and (not error)
               (not loading)
               (not= query-params new-query-params)
               (or (= (:active-panel db) required-active-panel)
                   (not required-active-panel)))
      (f/dispatch [:fetch path new-query-params]))
    val))

