(ns fractalify.components.api-wrap
  (:require [reagent.core :as r]
            [re-frame.core :as f]
            [fractalify.main.schemas :as mch]
            [schema.core :as s :include-macros true]
            [reagent.impl.util :as ru]
            [fractalify.utils :as u]
            [reagent.impl.batching :as rb]
            [plumbing.core :as p]))


(defn ^:private api-wrap
  [api-route path value-sub route-param-names force-reload]
  (let [val (f/subscribe (u/ensure-seq value-sub))
        loading? (f/subscribe [:loading? path])
        dispatch #(f/dispatch [:api-fetch
                               {:api-route         api-route
                                :path              path
                                :query-params      %
                                :route-param-names route-param-names
                                :force-reload      force-reload}])]
    (r/create-class
      {:component-will-mount
       (fn [this]
         (dispatch (r/props this)))
       :component-will-receive-props
       (fn [_ new-argv]
         (dispatch (ru/extract-props new-argv)))
       :reagent-render
       (fn [_ child]
         (conj child @val @loading?))})))

(def ApiWrapConfig
  {:api-route                          s/Keyword
   :path                               mch/DbPath
   :value-sub                          (s/cond-pre s/Keyword [s/Keyword])
   :query-params-sub                   (s/cond-pre s/Keyword [s/Keyword])
   (s/optional-key :route-param-names) [s/Keyword]
   (s/optional-key :force-reload)      s/Bool})

(s/defn ^:private api-query-params-wrap [config :- ApiWrapConfig]
  (p/letk [[api-route path value-sub query-params-sub {route-param-names []} {force-reload nil}] config
           query-params (f/subscribe (u/ensure-seq query-params-sub))
           f (partial api-wrap api-route path value-sub route-param-names force-reload)]
    (fn [child]
      [f @query-params child])))

(s/defn create-api-wrap [config :- ApiWrapConfig]
  (partial api-query-params-wrap config))