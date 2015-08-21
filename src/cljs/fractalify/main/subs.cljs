(ns fractalify.main.subs
    (:require-macros [reagent.ratom :refer [reaction]])
    (:require [re-frame.core :as r]))

(r/register-sub
 :name
 (fn [db]
   (reaction (:name @db))))

(r/register-sub
 :active-panel
 (fn [db _]
   (reaction (:active-panel @db))))

(r/register-sub
  :form-errors
  (fn [db vec]
    (reaction (get-in @db [:forms (second vec) :errors]))))