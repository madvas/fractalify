(ns fractalify.users.routes
  (:require-macros [secretary.core :refer [defroute]])
  (:import goog.History)
  (:require [re-frame.core :as r]
            [fractalify.router :as t]
            [fractalify.users.components.login-join :as login-join]
            [fractalify.users.components.forgot-pass :as forgot-pass]
            [fractalify.users.components.change-pass :as change-pass]
            [fractalify.users.components.edit-profile :as edit-profile]
            [fractalify.components.content :as content]))

(defn app-routes []
  (t/add-route! :login
                (defroute "/login" []
                          (r/dispatch [:set-active-panel :login])))
  (defmethod t/panels :login [] (login-join/login-join :login))


  (t/add-route! :join
                (defroute "/join" []
                          (r/dispatch [:set-active-panel :join])))
  (defmethod t/panels :join [] (login-join/login-join :join))


  (t/add-route! :forgot-password
                (defroute "/forgot-password" []
                          (r/dispatch [:set-active-panel :forgot-password])))
  (defmethod t/panels :forgot-password [] [forgot-pass/forgot-pass])


  (t/add-route! :change-password
                (defroute "/change-password" []
                          (r/dispatch [:set-active-panel :change-password])))
  (defmethod t/panels :change-password [] [content/content [change-pass/change-pass]])


  (t/add-route! :edit-profile
                (defroute "/edit-profile" []
                          (r/dispatch [:set-active-panel :edit-profile])))
  (defmethod t/panels :edit-profile [] [content/content [edit-profile/edit-profile]])

  (t/add-route! :logout
                (defroute "/logout" []
                          (r/dispatch [:logout])
                          (t/go! :home))))
