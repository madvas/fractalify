(ns fractalify.users.components.edit-profile
  (:require [material-ui.core :as ui]
            [fractalify.validators :as v]
            [fractalify.components.paper-panel :as paper-panel]
            [re-frame.core :as f]
            [fractalify.components.email :as email]
            [fractalify.components.text-field :as text-field]
            [fractalify.components.form-text :as form-text]))

(def user-bio-maxlen 140)

(defn edit-profile []
  (let [form-errors (f/subscribe [:users-form-errors :edit-profile])
        user (f/subscribe [:logged-user])]
    (fn []
      [paper-panel/paper-panel
       [:div.col-xs-12
        [:h1 "Edit your profile"]]
       [:div.col-xs-12
        [text-field/text-field [:username]
         {:floating-label-text "Username"
          :disabled            true}]]
       [:div.col-xs-12
        [email/email [:users :edit-profile :email]
         {:default-value (:email @user)}]]
       [:div.col-xs-12
        [form-text/form-text [:users :edit-profile :bio]
         {:floating-label-text "Bio"
          :multiLine           true
          :default-value       (:bio @user)
          :validators          [(partial v/length 0 user-bio-maxlen)]}]]
       [:div.col-xs-12
        [:div.row.col-xs-12.mar-top-20
         [:div.col-xs-12.col-sm-6.col-sm-offset-6
          [ui/flat-button {:label      "Save"
                           :primary    true
                           :disabled   (not (empty? @form-errors))
                           :onTouchTap #(f/dispatch [:edit-profile])}]]]]])))
