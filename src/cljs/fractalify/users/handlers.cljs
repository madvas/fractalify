(ns fractalify.users.handlers
  (:require-macros [fractalify.tracer-macros :refer [trace-handlers]])
  (:require [fractalify.middleware :as m]
            [re-frame.core :as r]
            [fractalify.db-utils :as d]
            [re-frame.core :as f]
            [fractalify.router :as t]
            [fractalify.tracer]
            [fractalify.utils :as u]
            [plumbing.core :as p]
            ))

(trace-handlers
  (r/register-handler
    :login
    m/standard-middlewares
    (fn [db _]
      (f/dispatch
        [:api-post
         {:api-route     :login
          :params        (d/get-form-data db :users :login)
          :handler       :login-resp
          :error-handler :login-err}])
      db))

  (r/register-handler
    :login-resp
    m/standard-middlewares
    (fn [db [user]]
      (t/go! :home)
      (assoc-in db [:users :logged-user] user)))

  (r/register-handler
    :login-err
    m/standard-middlewares
    (fn [db [error]]
      (p/letk [[status] error]
        (if (= status 403)
          (f/dispatch [:show-snackbar "Sorry, these are wrong credentials"])
          )
        db)))

  (r/register-handler
    :join
    m/standard-middlewares
    (fn [db _]
      (f/dispatch [:api-send
                   :join
                   (d/get-form-data db :users :join)
                   :login-resp
                   :join-err])
      db))

  (r/register-handler
    :join-err
    m/standard-middlewares
    (fn [db [user]]
      ;TODO
      db))

  (r/register-handler
    :logout
    [m/standard-middlewares (f/undoable "logout")]
    (fn [db _]
      (println "here")
      (f/dispatch [:api-post
                   {:api-route :logout}])
      (t/go! :home)
      (u/dissoc-in db [:users :logged-user])))

  (r/register-handler
    :forgot-password
    [m/standard-middlewares (r/undoable "forgot-password")]
    (fn [db _]
      (f/dispatch [:api-send
                   :forgot-password
                   (d/get-form-data db :users :forgot-password)
                   :forgot-password-resp])
      (assoc-in db [:users :forms :forgot-password :email] "")
      db))

  (r/register-handler
    :forgot-password-resp
    m/standard-middlewares
    (fn [db _]
      (f/dispatch [:show-snackbar "Password was reset. Please check your email."])
      (t/go! :home)
      db))

  (r/register-handler
    :edit-profile
    [m/standard-middlewares (f/undoable "edit-profile")]
    (fn [db _]
      (let [profile (d/get-form-data db :users :edit-profile)]
        (f/dispatch [:api-send
                     :edit-profile
                     profile
                     :edit-profile-resp])
        (merge-with merge db {:logged-user profile}))
      db))

  (r/register-handler
    :edit-profile-resp
    m/standard-middlewares
    (fn [db _]
      (f/dispatch [:show-snackbar "Your profile was successfully saved."])
      (t/go! :home)
      db))

  (r/register-handler
    :change-password
    m/standard-middlewares
    (fn [db _]
      (f/dispatch [:api-send
                   :change-password
                   (d/get-form-data db :users :change-password)
                   :change-password-resp])
      db))

  (r/register-handler
    :change-password-resp
    m/standard-middlewares
    (fn [db _]
      (f/dispatch [:show-snackbar "Your password has been successfully changed."])
      (t/go! :home)
      db))
  )
