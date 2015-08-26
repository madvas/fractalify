(ns fractalify.main.subs
  (:require-macros [reagent.ratom :refer [reaction]]
                   [fractalify.tracer-macros :refer [trace-subs]])
  (:require [re-frame.core :as r]
            [fractalify.main.handlers :as h]
            [clairvoyant.core :refer-macros [trace-forms]]
            [fractalify.tracer :refer [tracer]]
            [fractalify.utils :as u]))

(trace-subs
  (r/register-sub
    :db
    (fn [db]
      (reaction @db)))

  (r/register-sub
    :active-panel
    (fn [db _]
      (reaction (:active-panel @db))))

  (r/register-sub
    :route-params
    (fn [db _]
      (reaction (:route-params @db))))

  (r/register-sub
    :snackbar-props
    (fn [db [_]]
      (reaction (:snackbar-props @db))))

  (r/register-sub
    :form-errors
    (fn [db [_ form]]
      (reaction (get-in @db [:forms form :errors]))))

  (r/register-sub
    :get-form-item
    (fn [db [_ & path]]
      (reaction (get-in @db (into [:forms] path)))))

  (r/register-sub
    :form-data
    (fn [db [_ form]]
      (reaction (h/get-form-data @db form)))))



