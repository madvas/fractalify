(ns fractalify.components.text-field
  (:require-macros [fractalify.tracer-macros :refer [trace-views]]
                   [cljs.core.async.macros :refer [go]])
  (:require
    [reagent.core :as r]
    [fractalify.tracer]
    [material-ui.core :as ui]
    [re-frame.core :as f]
    [schema.core :as s :include-macros true]
    [fractalify.utils :as u]
    [fractalify.validators :as v]
    [fractalify.styles :as y]
    [plumbing.core :as p]
    [cljs.core.async :refer [chan >! <!]]
    [fractalify.main.schemas :as ch]))


(def style (merge y/w-100 {:padding-bottom 13 :text-align "left"}))
(def underline-style {:bottom 22})
(def error-style {:text-align "left"})

(declare text-field)

(defn parse-val
  [evt type]
  (-> (u/e-val evt)
      (p/?> (= type "number")
            u/parse-float)))

(defn- dirty? [this]
  (:dirty? (r/state this)))

(defn- set-dirty! [this value]
  (r/set-state this {:dirty? value}))

(defn- get-error [this]
  (:error (r/state this)))

(defn- set-error! [this value err-path]
  (f/dispatch (conj err-path value))
  (r/set-state this {:error value}))

(defn on-change [dispatch val]
  (f/dispatch (conj dispatch val)))

(s/defschema Value (s/maybe (s/cond-pre s/Str s/Num)))

(defn validate [val props]
  (let [validators (u/concat-vec (when (:required props) [v/required])
                                 (:validators props))
        validator (apply u/or-fn validators)]
    (validator val)))

(s/defn text-field
  ([value floating-label-text props]
    (text-field value floating-label-text nil nil props))
  ([value
    floating-label-text :- s/Str
    path :- (s/maybe ch/DbPath)
    err-path :- (s/maybe ch/DbPath)
    props :- {s/Keyword s/Any}]
    (let [debounced-change (u/debounce #(on-change path %) (:debounce props))]
      (s/fn [value :- Value _ _ _ props]
        (let [this (r/current-component)]
          [ui/text-field
           (merge
             {:default-value       value
              :floating-label-text floating-label-text
              :errorText           (when (dirty? this) (get-error this))
              :style               style
              :underline-style     underline-style
              :error-style         error-style}
             (when path
               {:on-change (fn [evt]
                             (let [val (parse-val evt (:type props))
                                   error (validate val props)]
                               (set-dirty! this true)
                               (set-error! this error err-path)
                               (when-not (and (:stop-dispatch-on-error props) error)
                                 (debounced-change val))))})
             props)])))))