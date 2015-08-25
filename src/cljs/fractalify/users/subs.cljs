(ns fractalify.users.subs
  (:require-macros [reagent.ratom :refer [reaction]]
                   [fractalify.tracer-macros :refer [trace-subs]])
  (:require [re-frame.core :as r]
            [fractalify.tracer :refer [tracer]]))

(trace-subs
  (r/register-sub
    :user
    (fn [db _]
      (reaction (:user @db))))

  (r/register-sub
    :username
    (fn [db _]
      (reaction (get-in @db [:user :username])))))