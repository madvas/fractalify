(ns fractalify.users.subs
  (:require-macros [reagent.ratom :refer [reaction]]
                   [fractalify.tracer-macros :refer [trace-subs]])
  (:require [re-frame.core :as f]
            [fractalify.tracer :refer [tracer]]))

(f/register-sub
  :logged-user
  (fn [db _]
    (reaction (get-in @db [:users :logged-user]))))

(f/register-sub
  :username
  (fn [_]
    (let [user (f/subscribe [:logged-user])]
      (reaction (:username @user)))))

(f/register-sub
  :user-detail
  (fn [db _]
    (reaction (get-in @db [:users :user-detail]))))

(f/register-sub
  :users-form-errors
  (fn [_ path & args]
    (let [errors (apply f/subscribe (into [:form-errors :users] path) args)]
      (reaction @errors))))

(f/register-sub
  :my-user-detail?
  (fn [db _]
    (let [logged-user (f/subscribe [:logged-user])]
      (reaction (= (:username (get-in @db [:users :user-detail]))
                   (:username @logged-user))))))