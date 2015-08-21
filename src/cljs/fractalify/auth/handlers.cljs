(ns fractalify.auth.handlers
  (:require [fractalify.middleware :as m]
            [re-frame.core :as r]
            [fractalify.main.handlers :as h]))

(r/register-handler
  :login
  m/standard-middlewares
  (fn [db _]
    (let [creds (h/get-form-data db :login)]
      (assoc db :user {:username (:user creds)
                       :email    "some@email.com"}))))