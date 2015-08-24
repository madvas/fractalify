(ns fractalify.users.handlers
  (:require [fractalify.middleware :as m]
            [re-frame.core :as r]
            [fractalify.main.handlers :as h]
            [re-frame.core :as f]
            [fractalify.router :as t]))

(r/register-handler
  :login
  m/standard-middlewares
  (fn [db _]
    (let [creds (h/get-form-data db :login)]
      (assoc db :user {:username (:user creds)
                    :email    "some@email.com"
                    :bio      "oh my bio"}))))

(r/register-handler
  :join
  m/standard-middlewares
  (fn [db _]
    (let [creds (h/get-form-data db :join)]
      (assoc db :user (select-keys creds [:username :email])))))

(r/register-handler
  :logout
  m/standard-middlewares
  (fn [db _]
    (dissoc db :user)))

(r/register-handler
  :forgot-password
  m/standard-middlewares
  (fn [db _]
    (let [email (:email (h/get-form-data db :forgot-password))]
      (f/dispatch [:show-snackbar {:message "Password was reset. Please check your email."}])
      (t/go! :home)
      (assoc-in db [:forms :forgot-password :email] ""))))

(r/register-handler
  :edit-profile
  m/standard-middlewares
  (fn [db _]
    (let [profile (h/get-form-data db :edit-profile)]
      (f/dispatch [:show-snackbar {:message "Your profile was successfully saved."}])
      (t/go! :home)
      (merge-with merge db {:user profile}))))
