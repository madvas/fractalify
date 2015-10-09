(ns fractalify.users.routes
  (:require [re-frame.core :as r]
            [fractalify.router :as t]
            [fractalify.users.components.login-join :as login-join]
            [fractalify.users.components.forgot-pass :as forgot-pass]
            [fractalify.users.components.change-pass :as change-pass]
            [fractalify.users.components.edit-profile :as edit-profile]
            [fractalify.users.components.user-detail :as user-detail]
            [fractalify.users.components.reset-pass :as reset-pass]))

(defn define-routes! []

  (t/add-routes! {"login"                                  :login
                  "join"                                   :join
                  "logout"                                 :logout
                  "forgot-password"                        :forgot-password
                  ["reset-password/" :username "/" :token] :reset-password
                  "change-password"                        :change-password
                  "edit-profile"                           :edit-profile
                  ["users/" :username]                     :user-detail})

  (defmethod t/panels :login [] (login-join/login-join :login))
  (defmethod t/panels :join [] (login-join/login-join :join))
  (defmethod t/panels :forgot-password [] [forgot-pass/forgot-pass])
  (defmethod t/panels :change-password [] [change-pass/change-pass])
  (defmethod t/panels :reset-password [] [reset-pass/reset-pass])
  (defmethod t/panels :edit-profile [] [edit-profile/edit-profile])
  (defmethod t/panels :user-detail [] [user-detail/user-detail]))
