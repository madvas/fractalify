(ns fractalify.users.handlers
  (:require-macros [fractalify.tracer-macros :refer [trace-handlers]])
  (:require [fractalify.middleware :as m]
            [re-frame.core :as r]
            [fractalify.db-utils :as d]
            [re-frame.core :as f]
            [fractalify.router :as t]
            [fractalify.tracer]))

(trace-handlers
  (r/register-handler
    :login
    m/standard-middlewares
    (fn [db _]
      (let [creds (d/get-form-data db :users :login)]
        (assoc db :logged-user {:username (:user creds)
                                :email    "some@email.com"
                                :bio      "oh my bio"}))))

  (r/register-handler
    :join
    m/standard-middlewares
    (fn [db _]
      (let [creds (d/get-form-data db :users :join)]
        (assoc db :logged-user (select-keys creds [:username :email])))))

  (r/register-handler
    :logout
    m/standard-middlewares
    (fn [db _]
      (dissoc db :logged-user)))

  (r/register-handler
    :forgot-password
    m/standard-middlewares
    (fn [db _]
      (let [email (:email (d/get-form-data :users db :forgot-password))]
        (f/dispatch [:show-snackbar "Password was reset. Please check your email."])
        (t/go! :home)
        (assoc-in db [:forms :forgot-password :email] ""))))

  (r/register-handler
    :edit-profile
    m/standard-middlewares
    (fn [db _]
      (let [profile (d/get-form-data db :users :edit-profile)]
        (f/dispatch [:show-snackbar "Your profile was successfully saved."])
        (t/go! :home)
        (merge-with merge db {:logged-user profile})))))
