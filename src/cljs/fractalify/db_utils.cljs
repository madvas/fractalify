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

(s/defn assoc-query-params [db path :- ch/DbPath v]
  (println "assoc-query-params" path)
  (assoc-in db [:queries path] {:query-params v}))

(s/defn assoc-loading [db path :- ch/DbPath v]
  (println "assoc-loading" path)
  (assoc-in db [:queries path :loading?] v))

(s/defn loading? [db path :- ch/DbPath]
  (get-in db [:queries path :loading?]))

(s/defn insert-queryable
  ([db
    path :- ch/DbPath
    val
    query-params :- ch/QueryParams]
   (insert-queryable db path val query-params path))
  ([db instar-path val query-params path]
   (-> db
       (i/transform db instar-path val)
       (assoc-query-params path query-params))))

(s/defn query
  [db
   path :- ch/DbPath
   new-query-params :- ch/QueryParams
   endpoint-key :- s/Keyword
   required-active-panel :- s/Keyword]
  (p/letk [[{query-params nil} {loading? nil} {error nil}] (or (get-in db [:queries path]) {})]
    #_(when (= path [:fractals :fractal-detail :comments])
        (do
          (println "query" path new-query-params)
          (u/p "old-q:" query-params)
          (u/p "new-q:" new-query-params)
          (u/p "loading:" loading?)))
    (when (and (not error)
             (not loading?)
             (not= query-params new-query-params)
             (or (= (:active-panel db) required-active-panel)
                 (not required-active-panel)))
      (when (= path [:fractals :fractal-detail :comments])
        (println "query" path new-query-params)
        (u/p "old-q:" query-params)
        (u/p "new-q:" new-query-params)
        (u/p "loading:" loading?))
      (f/dispatch [:api-fetch endpoint-key path new-query-params]))
    (get-in db path)))

