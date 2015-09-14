(ns fractalify.components.api-wrap
  (:require [reagent.core :as r]
            [re-frame.core :as f]
            [fractalify.main.schemas :as mch]
            [schema.core :as s :include-macros true]
            [reagent.impl.util :as ru]
            [fractalify.utils :as u]
            [reagent.impl.batching :as rb]
            [plumbing.core :as p]))


(defn- dispatch [endpoint-key path query-params force-reload]
  (f/dispatch [:api-fetch endpoint-key path query-params force-reload]))

(defn ^:private api-wrap
  [endpoint-key path value-sub force-reload]
  (let [val (f/subscribe (u/ensure-vec value-sub))
        loading? (f/subscribe [:loading? path])]
    (r/create-class
      {:component-will-mount
       (fn [this]
         (dispatch endpoint-key path (r/props this) force-reload))
       :component-will-receive-props
       (fn [_ new-argv]
         (dispatch endpoint-key path (ru/extract-props new-argv) force-reload))
       :reagent-render
       (fn [_ child]
         (conj child @val @loading?))})))

(p/defnk ^:private api-query-params-wrap [endpoint-key path value-sub query-params-sub :as config]
  (let [query-params (f/subscribe (u/ensure-vec query-params-sub))
        f (partial api-wrap endpoint-key path value-sub (:force-reload config))]
    (fn [child]
      [f @query-params child])))

(def ApiWrapConfig
  {:endpoint-key                  s/Keyword
   :path                          mch/DbPath
   :value-sub                     (s/cond-pre s/Keyword [s/Keyword])
   :query-params-sub              (s/cond-pre s/Keyword [s/Keyword])
   (s/optional-key :force-reload) s/Bool})

(p/defnk create-api-wrap
  [endpoint-key path value-sub query-params-sub :as config] :- ApiWrapConfig
  (partial api-query-params-wrap config))