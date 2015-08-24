(ns fractalify.main.subs
    (:require-macros [reagent.ratom :refer [reaction]])
    (:require [re-frame.core :as r]))

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
  :get-form-item
  (fn [db [_ form item]]
    (reaction (get-in @db [:forms form item]))))

(r/register-sub
  :form-errors
  (fn [db [_ form]]
    (reaction (get-in @db [:forms form :errors]))))

(r/register-sub
  :snackbar-props
  (fn [db [_]]
    (reaction (:snackbar-props @db))))