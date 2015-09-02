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
            [clojure.set :as set]))

(defn get-form-data [db form]
  (-> db
      (get-in [:forms form])
      (dissoc :errors)))


(trace-handlers
  #_ {:tracer (fractalify.tracer/tracer :color "green")}

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
    (fn [db params]
      (let [value (last params)
            path (into [] (butlast params))]
        (if-let [key (:key (last path))]
          (u/println (update-in db (into [:forms] (butlast path)) set/rename-keys {key value}))
          (assoc-in db (into [:forms] path) value)))))

  (r/register-handler
    :set-form-error
    m/standard-without-debug
    (fn [db [form-name & params]]
      (let [value (last params)
            item-path (into [] (butlast params))
            path (into [:forms form-name :errors] item-path)]
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
      db)))