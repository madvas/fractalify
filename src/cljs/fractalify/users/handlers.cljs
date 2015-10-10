(ns fractalify.users.handlers
  (:require-macros [fractalify.tracer-macros :refer [trace-handlers]])
  (:require [fractalify.middleware :as m]
            [re-frame.core :as r]
            [fractalify.handler-utils :as d]
            [re-frame.core :as f]
            [fractalify.router :as t]
            [fractalify.tracer]
            [fractalify.utils :as u]))

(defn login-user [db user]
  (t/go! :home)
  (assoc-in db [:users :logged-user] user))


(r/register-handler
  :login
  m/standard-middlewares
  (fn [db _]
    (f/dispatch
      [:api-post
       {:api-route     :login
        :params        (d/get-form-data db :users :login)
        :handler       :login-resp
        :error-handler {403 "Sorry, these are wrong credentials"}}])
    db))

(r/register-handler
  :login-resp
  m/standard-middlewares
  (fn [db [user]]
    (login-user db user)))

(r/register-handler
  :join
  m/standard-middlewares
  (fn [db _]
    (f/dispatch
      [:api-put
       {:api-route     :join
        :params        (d/get-form-data db :users :join)
        :handler       :login-resp
        :error-handler {409 "Sorry, this account already exists. Please choose other"}}])
    db))

(r/register-handler
  :logout
  [m/standard-middlewares (f/undoable "logout")]
  (fn [db _]
    (f/dispatch [:api-post
                 {:api-route   :logout
                  :error-undo? true}])
    (t/go! :home)
    (u/dissoc-in db [:users :logged-user])))

(r/register-handler
  :forgot-password
  [m/standard-middlewares (r/undoable "forgot-password")]
  (fn [db _]
    (f/dispatch [:api-post
                 {:api-route   :forgot-password
                  :params      (d/get-form-data db :users :forgot-password)
                  :handler     #(d/snack-n-go! "Password was reset. Please check your email." :home)
                  :error-undo? true}])
    (assoc-in db [:users :forms :forgot-password :email] "")
    db))

(r/register-handler
  :edit-profile
  [m/standard-middlewares (f/undoable "edit-profile")]
  (fn [db _]
    (let [profile (d/get-form-data db :users :edit-profile)]
      (f/dispatch [:api-post
                   {:api-route    :edit-profile
                    :route-params (u/select-key (d/logged-user db) :username)
                    :params       profile
                    :handler      #(d/show-snackbar "Your profile was successfully saved.")
                    :error-undo?  true}])
      (update-in db [:users :logged-user] (u/partial-right merge profile)))))


(r/register-handler
  :change-password
  m/standard-middlewares
  (fn [db _]
    (f/dispatch [:api-post
                 {:api-route    :change-password
                  :route-params (u/select-key (d/logged-user db) :username)
                  :params       (d/get-form-data db :users :change-password)
                  :handler      #(d/show-snackbar "Your password has been successfully changed.")}])
    db))

(r/register-handler
  :reset-password
  m/standard-middlewares
  (fn [db _]
    (let [form-data (d/get-form-data db :users :reset-password)]
      (f/dispatch [:api-post
                   {:api-route     :reset-password
                    :params        form-data
                    :route-params  (u/select-key form-data :username)
                    :handler       #(d/snack-n-go! "Your password has been reset. You can login now" :login)
                    :error-handler {401 "Sorry, your token is invalid or expired"}}]))
    db))


