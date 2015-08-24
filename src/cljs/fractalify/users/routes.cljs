(ns fractalify.users.routes
  (:require [re-frame.core :as r]
            [fractalify.router :as t]
            [fractalify.users.components.login-join :as login-join]
            [fractalify.users.components.forgot-pass :as forgot-pass]
            [fractalify.users.components.change-pass :as change-pass]
            [fractalify.users.components.edit-profile :as edit-profile]))

(defn define-routes! []

  (t/add-routes! {"login"              :login
                  "join"               :join
                  "logout"             :logout
                  "forgot-password"    :forgot-password
                  "change-password"    (t/perm :change-password [:login-required])
                  "edit-profile"       (t/perm :edit-profile [:login-required])
                  ["users/" :username] :user-view})

  (defmethod t/panels :login [] (login-join/login-join :login))
  (defmethod t/panels :join [] (login-join/login-join :join))
  (defmethod t/panels :forgot-password [] [forgot-pass/forgot-pass])
  (defmethod t/panels :change-password [] [change-pass/change-pass])
  (defmethod t/panels :edit-profile [] [edit-profile/edit-profile]))
