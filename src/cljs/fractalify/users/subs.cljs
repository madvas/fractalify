(ns fractalify.users.subs
  (:require-macros [reagent.ratom :refer [reaction]]
                   [fractalify.tracer-macros :refer [trace-subs]])
  (:require [re-frame.core :as r]
            [fractalify.tracer :refer [tracer]]))

(r/register-sub
  :logged-user
  (fn [db _]
    (reaction (get-in @db [:users :logged-user]))))

(r/register-sub
  :username
  (fn [_]
    (let [user (r/subscribe [:logged-user])]
      (reaction (:username @user)))))

(r/register-sub
  :users-form-errors
  (fn [_ path & args]
    (let [errors (apply r/subscribe (into [:form-errors :users] path) args)]
      (reaction @errors))))