(ns fractalify.main.handlers
  (:require [re-frame.core :as r]
            [fractalify.db :as db]
            [fractalify.middleware :as m]
            [fractalify.utils :as u]
            [fractalify.components.snackbar :as snackbar]))

(defn get-form-data [db form]
  (-> db
      (get-in [:forms form])
      (dissoc :errors)))

(r/register-handler
  :initialize-db
  m/standard-middlewares
  (fn [_]
    db/default-db))

(r/register-handler
  :set-active-panel
  m/standard-middlewares
  (fn [db [active-panel]]
    (assoc db :active-panel active-panel)))

(r/register-handler
  :set-form-item
  m/standard-middlewares
  (fn [db [form item value]]
    (assoc-in db [:forms form item] value)))

(r/register-handler
  :set-form-error
  m/trim-validate
  ;m/standard-middlewares
  ;[m/trim-validate m/print-db]
  (fn [db [form field value]]
    (let [path [:forms form :errors field]]
      (if value
        (assoc-in db path value)
        (u/dissoc-in db path)))))

(r/register-handler
  :show-snackbar
  m/standard-middlewares
  (fn [db [snackbar-props]]
    (let [db (assoc db :snackbar-props snackbar-props)]
      (snackbar/show-snackbar!)
      db)))