(ns fractalify.fractals.subs
  (:require-macros [reagent.ratom :refer [reaction]])
  (:require [re-frame.core :as r]))

#_(r/register-sub
  :user
  (fn [db _]
    (reaction (:user @db))))
