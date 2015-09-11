(ns fractalify.main.subs
  (:require-macros [reagent.ratom :refer [reaction]]
                   [fractalify.tracer-macros :refer [trace-subs]])
  (:require [fractalify.main.handlers :as h]
            [clairvoyant.core :refer-macros [trace-forms]]
            [fractalify.tracer :refer [tracer]]
            [fractalify.utils :as u]
            [fractalify.db-utils :as d]
            [re-frame.core :as f]))

(trace-subs
  (f/register-sub
    :active-panel
    (fn [db _]
      (reaction (:active-panel @db))))

  (f/register-sub
    :route-params
    (fn [db _]
      (reaction (:route-params @db))))

  (f/register-sub
    :snackbar-props
    (fn [db [_]]
      (reaction (:snackbar-props @db))))

  (f/register-sub
    :dialog-props
    (fn [db [_]]
      (reaction (:dialog-props @db))))

  (f/register-sub
    :form-errors
    (fn [db [_ module form]]
      (reaction (get-in @db [module :forms form :errors]))))

  (f/register-sub
    :form-item
    (fn [db [_ module & path]]
      (reaction
        (if-let [key (:key (last path))]
          key
          (get-in @db (into [module :forms] path))))))

  (f/register-sub
    :form-data
    (fn [db [_ module form]]
      (reaction (d/get-form-data @db module form)))))



