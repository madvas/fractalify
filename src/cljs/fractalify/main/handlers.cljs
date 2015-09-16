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
            [re-frame.core :as f]
            [fractalify.db-utils :as d]
            [schema.core :as s :include-macros true]
            [fractalify.main.schemas :as ch]))

(defn default-send-err-handler [err]
  (f/dispatch [:undo])
  (f/dispatch [:show-snackbar "Oops, something went awfully wrong :("]))

(trace-handlers
  #_{:tracer (fractalify.tracef/tracer :color "green")}


  (f/register-handler
    :initialize-db
    m/standard-no-debug
    (fn [_]
      db/default-db))

  (f/register-handler
    :set-active-panel
    m/standard-middlewares
    (fn [db [active-panel permissions route-params]]
      (sidenav/close-sidenav!)
      (if-let [error (p/validate-permissions db permissions)]
        (do (f/dispatch [:show-snackbar (:message error)])
            (t/go! (:redirect error))
            db)
        (assoc db :active-panel active-panel
                  :route-params route-params))))

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
    :form-error
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
    :api-fetch
    m/standard-middlewares
    (fn [db [endpoint-key path query-params force-reload]]
      (if (or force-reload
              (not= (d/path-query-params db path) query-params))
        (do (u/mwarn "fetching " path query-params)
            (api/fetch! endpoint-key query-params
                        #(f/dispatch [:process-fetch-response path query-params %])
                        #(f/dispatch [:process-fetch-response-err path query-params %]))
            (d/assoc-query-loading db path true))
        db)))

  (f/register-handler
    :process-fetch-response
    m/standard-middlewares
    (fn [db [path query-params val]]
      (-> db
          (assoc-in path val)
          (d/assoc-path-query-params path query-params))))

  (f/register-handler
    :api-send
    m/standard-middlewares
    (fn [db [endpoint-key body-params on-succes on-err]]
      (let [on-succes (if (keyword? on-succes) (u/create-dispatch on-succes) on-succes)
            on-err (if (keyword? on-err) (u/create-dispatch on-err) on-err)]
        (u/mwarn "posting " endpoint-key body-params)
        (api/send! endpoint-key body-params
                   (or on-succes identity)
                   (or on-err default-send-err-handler)))
      db))

  )

