(ns fractalify.main.handlers
  (:require-macros [fractalify.tracer-macros :refer [trace-handlers]]
                   [clairvoyant.core :refer [trace-forms]])
  (:require [fractalify.db :as db]
            [fractalify.middleware :as m]
            [fractalify.utils :as u]
            [fractalify.components.snackbar :as snackbar]
            [fractalify.router :as t]
            [fractalify.main.components.sidenav :as sidenav]
            [fractalify.permissons :as p]
            [fractalify.tracer :refer [tracer]]
            [clojure.set :as set]
            [instar.core :as i]
            [fractalify.components.dialog :as dialog]
            [fractalify.api :as api]
            [clojure.string :as str]
            [re-frame.core :as f]))

(trace-handlers
  #_{:tracer (fractalify.tracef/tracer :color "green")}

  (f/register-handler
    :assoc-db
    m/standard-middlewares
    (fn [db [key value]]
      (assoc db key value)))

  (f/register-handler
    :dissoc-db
    m/standard-middlewares
    (fn [db [key]]
      (dissoc db key)))

  (f/register-handler
    :initialize-db
    m/standard-no-debug
    (fn [_]
      db/default-db))

  (f/register-handler
    :set-active-panel
    m/standard-middlewares
    (fn [db [active-panel permissions]]
      (sidenav/close-sidenav!)
      (if-let [error (p/validate-permissions db permissions)]
        (do (f/dispatch [:show-snackbar (:message error)])
            (t/go! (:redirect error))
            db)
        (assoc db :active-panel active-panel))))

  (f/register-handler
    :form-item
    m/standard-middlewares
    (fn [db [module & params]]
      (let [value (last params)
            path (vec (butlast params))]
        (if-let [key (:key (last path))]
          (update-in db (into [module :forms] (butlast path)) set/rename-keys {key value})
          (assoc-in db (into [module :forms] path) value)))))

  (f/register-handler
    :dissoc-form-item
    m/standard-middlewares
    (fn [db module path]
      (u/dissoc-in db (into [module :forms] path))))

  (f/register-handler
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

  (f/register-handler
    :show-snackbar
    m/standard-no-debug
    (fn [db [msg & snackbar-props]]
      (snackbar/show-snackbar!)
      (assoc db :snackbar-props
                (assoc snackbar-props :message msg))))

  (f/register-handler
    :show-dialog
    m/standard-no-debug
    (fn [db [dialog-props]]
      (let [db (assoc db :dialog-props dialog-props)]
        (dialog/show-dialog!)
        db)))

  (f/register-handler
    :fetch
    m/standard-middlewares
    (fn [db [path query-params]]
      (println "fetching " path query-params)
      (let [url (str/join "/" (map name path))]
        (api/request! url query-params
                      #(f/dispatch [:process-response path query-params %])
                      #(f/dispatch [:process-response-err path query-params %])))
      (update-in db path #(vary-meta % assoc :loading true))))

  (f/register-handler
    :process-response
    m/standard-middlewares
    (fn [db [path query-params value]]
      (println ":process-response")
      (-> db
          (update-in path #(with-meta value {:query-params query-params})))))

  )

