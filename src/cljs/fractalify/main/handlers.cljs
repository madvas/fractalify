(ns fractalify.main.handlers
  (:require-macros [fractalify.tracer-macros :refer [trace-handlers]]
                   [clairvoyant.core :refer [trace-forms]])
  (:require [fractalify.db :as db]
            [fractalify.middleware :as m]
            [fractalify.utils :as u]
            [fractalify.components.snackbar :as snackbar]
            [fractalify.router :as t]
            [fractalify.main.components.sidenav :as sidenav]
            [fractalify.tracer :refer [tracer]]
            [fractalify.components.dialog :as dialog]
            [fractalify.api :as api]
            [re-frame.core :as f]
            [fractalify.handler-utils :as d]
            [schema.core :as s :include-macros true]
            [plumbing.core :as p]
            [fractalify.ga :as ga]))

(defn api-send! [params]
  (-> params
      (update :handler d/create-handler)
      (update :error-handler (partial d/create-send-error-handler (:error-undo? params)))
      api/send!))

(f/register-handler
  :initialize
  m/standard-no-debug
  (fn [_]
    (f/dispatch [:api-fetch
                 {:api-route :logged-user
                  :path      [:users :logged-user]}])
    db/default-db))

(f/register-handler
  :set-active-panel
  m/standard-middlewares
  (fn [db [active-panel route-params]]
    (sidenav/close-sidenav!)
    (ga/send-page-view active-panel route-params)
    (assoc db :active-panel active-panel
              :route-params route-params)))

(f/register-handler
  :set-form-item
  m/standard-middlewares
  (fn [db [module & params]]
    (let [value (last params)
          path (vec (butlast params))]
      (assoc-in db (into [module :forms] path) value))))

(f/register-handler
  :set-form
  m/standard-middlewares
  (fn [db [module form value merge?]]
    (update-in db [module :forms form] #(if merge? (merge % value) value))))

(f/register-handler
  :dissoc-form-item
  m/standard-middlewares
  (fn [db [module & path]]
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
  (fn [db [opts]]
    (p/letk [[api-route
              path
              {query-params {}}
              {route-param-names []}
              {force-reload false}
              {handler #(f/dispatch [:default-fetch-resp path query-params %])}
              {error-handler :default-fetch-resp-err}
              ] opts]
            (if (or force-reload
                    (not= (d/path-query-params db path) query-params))
              (do (api/fetch! api-route query-params route-param-names
                              {:handler       (d/create-handler handler)
                               :error-handler (d/create-handler error-handler)})
                  (d/assoc-query-loading db path true))
              db))))

(f/register-handler
  :default-fetch-resp
  m/standard-middlewares
  (fn [db [path query-params val]]
    (-> db
        (assoc-in path val)
        (d/assoc-path-query-params path query-params))))

(f/register-handler
  :default-fetch-resp-err
  m/standard-middlewares
  (fn [db [err]]
    (if (= 404 (:status err))
      (f/dispatch [:show-snackbar "Sorry, but this page was not found..."])
      (u/merror "Error while fetching " err))
    (ga/send-event :server-error :fetch (str err) (:status err))
    db))

(f/register-handler
  :api-put
  m/standard-middlewares
  (fn [db [opts]]
    (api-send! (merge {:method :put} opts))
    db))

(f/register-handler
  :api-post
  m/standard-middlewares
  (fn [db [opts]]
    (api-send! (merge {:method :post :params {}} opts))
    db))

(f/register-handler
  :api-delete
  m/standard-middlewares
  (fn [db [opts]]
    (api-send! (merge {:method :delete :params {}} opts))
    db))


