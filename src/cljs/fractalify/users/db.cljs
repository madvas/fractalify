(ns fractalify.users.db
  (:require [fractalify.utils :as u]
            [fractalify.users.schemas :as uch]))


(def default-db
  {:forms (merge
            (u/coerce-forms-with-defaults uch/UserForms)
            (when goog.DEBUG {:login           {:username "admin" :password "111111"}
                              :join            {:username     "newuser"
                                                :email        "some@email.com"
                                                :password     "111111"
                                                :confirm-pass "111111"
                                                :bio          ""}
                              :forgot-password {:email "matus.lestan@gmail.com"}
                              :change-password {:current-pass     "111111"
                                                :new-pass         "111111"
                                                :confirm-new-pass "111111"}}))})
