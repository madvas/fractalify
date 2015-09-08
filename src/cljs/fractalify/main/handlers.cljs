(ns fractalify.main.handlers
  (:require-macros [fractalify.tracer-macros :refer [trace-handlers]]
                   [clairvoyant.core :refer [trace-forms]])
  (:require [re-frame.core :as r]
            [fractalify.db :as db]
            [fractalify.middleware :as m]
            [fractalify.utils :as u]
            [fractalify.components.snackbar :as snackbar]
            [fractalify.router :as t]
            [fractalify.main.components.sidenav :as sidenav]
            [fractalify.permissons :as p]
            [fractalify.tracer :refer [tracer]]
            [clojure.set :as set]
            [instar.core :as i]
            [fractalify.components.dialog :as dialog]))

(trace-handlers
  #_{:tracer (fractalify.tracer/tracer :color "green")}

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
    m/standard-no-debug
    (fn [_]
      db/default-db))

  (r/register-handler
    :set-active-panel
    m/standard-middlewares
    (fn [db [active-panel permissions]]
      (if-let [error (p/validate-permissions db permissions)]
        (do (r/dispatch [:show-snackbar (:message error)])
            (t/go! (:redirect error))
            db)
        (assoc db :active-panel active-panel))))

  (r/register-handler
    :form-item
    m/standard-middlewares
    (fn [db [module & params]]
      (let [value (last params)
            path (vec (butlast params))]
        (if-let [key (:key (last path))]
          (update-in db (into [module :forms] (butlast path)) set/rename-keys {key value})
          (assoc-in db (into [module :forms] path) value)))))

  (r/register-handler
    :dissoc-form-item
    m/standard-middlewares
    (fn [db module path]
      (u/dissoc-in db (into [module :forms] path))))

  (r/register-handler
    :set-form-error
    m/standard-no-debug
    ;m/standard-middlewares
    (fn [db [module form-name & params]]
      (let [value (last params)
            item-path (into [] (butlast params))
            path (into [module :forms form-name :errors] item-path)]
        (if value
          (assoc-in db path value)
          (u/dissoc-in db path)))))

  (r/register-handler
    :show-snackbar
    m/standard-no-debug
    (fn [db [msg & snackbar-props]]
      (let [db (assoc db :snackbar-props
                         (assoc snackbar-props :message msg))]
        (snackbar/show-snackbar!)
        db)))

  (r/register-handler
    :show-dialog
    m/standard-no-debug
    (fn [db [snackbar-props]]
      (let [db (assoc db :dialog-props snackbar-props)]
        (dialog/show-dialog!)
        db)))

  (r/register-handler
    :hide-dialog
    m/standard-no-debug
    (fn [db _]
      (dialog/hide-dialog!)
      db))

  (r/register-handler
    :sidenav-action
    m/standard-no-debug
    (fn [db [action]]
      (condp = action
        :toggle (sidenav/toggle-sidenav!)
        :close (sidenav/close-sidenav!))
      db)))