(ns fractalify.main.subs-backup
  (:require-macros [reagent.ratom :refer [reaction]])
  (:require [re-frame.core :as r]
            [reagent.ratom :refer [make-reaction]]
            [fractalify.main.handlers :as h]
            [clairvoyant.core :refer-macros [trace-forms]]
            [fractalify.tracer :refer [tracer]]))

(trace-forms
  {:tracer (tracer :color "brown")}

  (r/register-sub
    :db
    (fn db-sub [db]
      (make-reaction
        (fn db-sub []
          @db))))

  (r/register-sub
    :active-panel
    (fn [db]
      (make-reaction
        (fn active-panel []
          (:active-panel @db)))))

  (r/register-sub
    :route-params
    (fn [db]
      (make-reaction
        (fn route-params []
          (:route-params @db)))))

  (r/register-sub
    :get-form-item
    (fn [db [_ form item index]]
      (make-reaction
        (fn get-form-item []
          (let [form-item (get-in @db [:forms form item])]
            (if (and (vector? form-item) index)
              (nth form-item index)
              form-item))))))

  (r/register-sub
    :form-data
    (fn [db [_ form]]
      (make-reaction
        (fn form-data []
          (h/get-form-data @db form)))))

  (r/register-sub
    :form-errors
    (fn [db [_ form]]
      (make-reaction
        (fn form-errors []
          (get-in @db [:forms form :errors])))))

  (r/register-sub
    :snackbar-props
    (fn [db [_]]
      (make-reaction
        (fn snackbar-props []
          (:snackbar-props @db)))))
  )

