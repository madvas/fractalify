(ns fractalify.db-utils
  (:require [fractalify.utils :as u]))

(def logged-user (u/partial-right get-in [:users :logged-user]))

(defn get-form-data [db module form]
  (-> db
      (get-in [module :forms form])
      (dissoc :errors)))