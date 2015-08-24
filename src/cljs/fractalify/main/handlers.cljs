(ns fractalify.main.handlers
  (:require [re-frame.core :as r]
            [fractalify.db :as db]
            [fractalify.middleware :as m]
            [fractalify.utils :as u]
            [fractalify.components.snackbar :as snackbar]
            [fractalify.router :as t]
            [fractalify.main.components.sidenav :as sidenav]
            [fractalify.permissons :as p]))

(defn get-form-data [db form]
  (-> db
      (get-in [:forms form])
      (dissoc :errors)))

(r/register-handler
  :assoc-db
  m/standard-middlewares
  (fn [db [key value]]
    (assoc db key value)))

(r/register-handler
  :dissoc-db
  m/standard-middlewares
  (fn [db [key]]
    (dissoc db key)))

(r/register-handler
  :initialize-db
  m/standard-without-debug
  (fn [_]
    db/default-db))

(r/register-handler
  :set-active-panel
  m/standard-middlewares
  (fn [db [active-panel permissions]]
    (if-let [error (p/validate-permissions db permissions)]
      (do (r/dispatch [:show-snackbar (select-keys error [:message])])
          (t/go! (:redirect error))
          db)
      (assoc db :active-panel active-panel))))

(r/register-handler
  :set-form-item
  m/standard-middlewares
  (fn [db [form item value]]
    (assoc-in db [:forms form item] value)))

(r/register-handler
  :set-form-error
  m/standard-without-debug
  (fn [db [form field value]]
    (let [path [:forms form :errors field]]
      (if value
        (assoc-in db path value)
        (u/dissoc-in db path)))))

(r/register-handler
  :show-snackbar
  m/standard-without-debug
  (fn [db [snackbar-props]]
    (let [db (assoc db :snackbar-props snackbar-props)]
      (snackbar/show-snackbar!)
      db)))

(r/register-handler
  :sidenav-action
  m/standard-without-debug
  (fn [db [action]]
    (cond
      (= action :toggle) (sidenav/toggle-sidenav!)
      (= action :close) (sidenav/close-sidenav!))
    db))