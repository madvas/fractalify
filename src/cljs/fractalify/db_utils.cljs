(ns fractalify.db-utils
  (:require [fractalify.utils :as u]
            [re-frame.core :as f]
            [plumbing.core :as p]
            [cljs.core]
            [schema.core :as s :include-macros true]
            [fractalify.main.schemas :as ch]
            [instar.core :as i]))

(def logged-user (u/partial-right get-in [:users :logged-user]))

(defn get-form-data [db module form]
  (-> db
      (get-in [module :forms form])
      (dissoc :errors)))

(s/defn path-query-params :- (s/maybe ch/QueryParams)
  [db path :- ch/DbPath]
  (:query-params (get-in db [:queries path])))

(s/defn assoc-path-query-params
  [db
   path :- ch/DbPath
   query-params :- ch/QueryParams]
  (assoc-in db [:queries path] {:query-params query-params}))

(s/defn assoc-query-loading [db path :- ch/DbPath v :- s/Bool]
  (assoc-in db [:queries path :loading?] v))

(s/defn query-loading? :- (s/maybe s/Bool)
  [db path :- ch/DbPath]
  (get-in db [:queries path :loading?]))

(s/defn assoc-with-query-params
  ([db
    path :- ch/DbPath
    val
    query-params :- ch/QueryParams]
    (assoc-with-query-params db path val query-params path))
  ([db instar-path val query-params path]
    (-> db
        (i/transform instar-path val)
        (assoc-path-query-params path query-params))))

