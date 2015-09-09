(ns fractalify.db-utils
  (:require [fractalify.utils :as u]
            [re-frame.core :as f]
            [plumbing.core :as p]))

(def logged-user (u/partial-right get-in [:users :logged-user]))

(defn get-form-data [db module form]
  (-> db
      (get-in [module :forms form])
      (dissoc :errors)))

(defn query [db path new-query-params]
  (p/letk [val (get-in db path)
           [query-params {loading nil} {error nil}] (meta val)]
    (println "query" path new-query-params)
    ;(u/p "old-q:" query-params)
    ;(u/p "new-q:" new-query-params)
    ;(u/p "loading:" loading)
    ;(u/p "error:" error)
    (when (and (not error)
               (not loading)
               (not= query-params new-query-params))
      (f/dispatch [:fetch path new-query-params]))
    val))