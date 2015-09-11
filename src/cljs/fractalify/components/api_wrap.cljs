(ns fractalify.components.api-wrap
  (:require [reagent.core :as r]
            [re-frame.core :as f]
            [fractalify.main.schemas :as mch]
            [schema.core :as s :include-macros true]
            [reagent.impl.util :as ru]
            [fractalify.utils :as u]
            [reagent.impl.batching :as rb]))


(defn- dispatch [endpoint-key path query-params]
  (f/dispatch [:api-fetch endpoint-key path query-params]))

(s/defn ^:private api-wrap
  [endpoint-key :- s/Keyword
   subscription :- s/Keyword
   path :- mch/DbPath
   query-params :- mch/QueryParams
   & args]
  (let [item (f/subscribe [subscription])]
    (r/create-class
      {:component-did-mount
       (fn [this]
         (println ":component-did-mount")
         (dispatch endpoint-key path (r/props this)))
       :component-will-receive-props
       (fn [_ new-argv]
         (println ":component-will-receive-props")
         (dispatch endpoint-key path (ru/extract-props new-argv)))
       :component-will-unmount
       (fn []
         (f/dispatch [:dissoc-db path]))
       :reagent-render
       (fn [_ child]
         (println ":reagent-render " (:id @item))
         [child @item])})))

(s/defn ^:private api-query-params-wrap
  [endpoint-key :- s/Keyword
   value-sub :- s/Keyword
   path :- mch/DbPath
   query-params-sub :- s/Keyword
   & args]
  (let [query-params (f/subscribe [query-params-sub])
        f (partial api-wrap endpoint-key value-sub path)]
    (fn [child]
      (println "api-query-params-wrap" @query-params)
      [f @query-params
       child])))

(s/defn create-api-wrap [& args]
  (apply partial api-query-params-wrap args))