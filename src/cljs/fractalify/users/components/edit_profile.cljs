(ns fractalify.users.components.edit-profile
  (:require [material-ui.core :as ui]
            [fractalify.validators :as v]
            [fractalify.components.paper-panel :as paper-panel]
            [re-frame.core :as f]
            [fractalify.components.email :as email]
            [fractalify.components.text-field :as text-field]
            [fractalify.components.form-text :as form-text]))

(defn edit-profile []
  (let [form-errors (f/subscribe [:form-errors :edit-profile])
        user (f/subscribe [:user])]
    (fn []
      [paper-panel/paper-panel
       [:div.col-xs-12
        [:h1 "Edit your profile"]]
       [:div.col-xs-12
        [text-field/text-field [:username]
         {:floating-label-text "Username"
          :disabled          true}]]
       [:div.col-xs-12
        [email/email :edit-profile :email
         {:default-value (:email @user)}]]
       [:div.col-xs-12
        [form-text/form-text [:edit-profile :bio]
         {:floating-label-text "Bio"
          :multiLine         true
          :default-value     (:bio @user)
          :validators        [v/user-bio]}]]
       [:div.col-xs-12
        [:div.row.col-xs-12.mar-top-20
         [:div.col-xs-12.col-sm-6.col-sm-offset-6
          [ui/flat-button {:label      "Save"
                           :primary    true
                           :disabled   (not (empty? @form-errors))
                           :onTouchTap #(f/dispatch [:edit-profile])}]]]]])))
