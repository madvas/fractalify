(ns fractalify.components.api-wrap
  (:require [reagent.core :as r]
            [re-frame.core :as f]
            [fractalify.main.schemas :as mch]
            [schema.core :as s :include-macros true]
            [reagent.impl.util :as ru]
            [fractalify.utils :as u]
            [reagent.impl.batching :as rb]
            [plumbing.core :as p]))


(defn- dispatch [endpoint-key path query-params]
  (f/dispatch [:api-fetch endpoint-key path query-params]))

(s/defn ^:private api-wrap
  [endpoint-key :- s/Keyword
   path :- mch/DbPath
   value-sub :- s/Keyword
   query-params :- mch/QueryParams
   & args]
  (let [val (f/subscribe [value-sub])
        loading? (f/subscribe [:loading? path])]
    (r/create-class
      {:component-will-mount
       (fn [this]
         (dispatch endpoint-key path (r/props this)))
       :component-will-receive-props
       (fn [_ new-argv]
         (dispatch endpoint-key path (ru/extract-props new-argv)))
       :reagent-render
       (fn [_ child]
         (conj child @val @loading?))})))

(s/defn ^:private api-query-params-wrap
  [endpoint-key :- s/Keyword
   path :- mch/DbPath
   value-sub :- s/Keyword
   query-params-sub :- s/Keyword
   & args]
  (let [query-params (f/subscribe [query-params-sub])
        f (partial api-wrap endpoint-key path value-sub)]
    (fn [child]
      [f @query-params child])))

(p/defnk create-api-wrap [endpoint-key path value-sub query-params-sub]
  (partial api-query-params-wrap endpoint-key path value-sub query-params-sub))