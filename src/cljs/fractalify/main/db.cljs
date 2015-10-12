(ns fractalify.main.db
  (:require [fractalify.utils :as u]
            [fractalify.main.schemas :as mch]))

(def default-db
  {:forms (u/coerce-forms-with-defaults mch/MainForms)})