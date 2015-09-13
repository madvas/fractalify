(ns fractalify.users.components.edit-profile
  (:require [material-ui.core :as ui]
            [fractalify.validators :as v]
            [fractalify.components.paper-panel :as paper-panel]
            [re-frame.core :as f]
            [fractalify.components.text-field :as text-field]
            [fractalify.components.form-text :as form-text]
            [fractalify.components.form :as form]))

(def user-bio-maxlen 140)

(defn edit-profile []
  (let [user (f/subscribe [:logged-user])]
    (fn []
      [form/form :users :edit-profile
       (fn [vals has-err?]
         (let [{:keys [email bio]} vals]
           [paper-panel/paper-panel
            [:div.col-xs-12
             [:h1 "Edit your profile"]]
            [:div.col-xs-12
             [text-field/text-field (:username @user) "Username" nil nil
              {:disabled true}]]
            [:div.col-xs-12
             [form-text/email email "Email" [:users :edit-profile :email]]]
            [:div.col-xs-12
             [form-text/text bio "Bio" [:users :edit-profile :bio]
              {:multi-line true
               :validators [(v/length 0 user-bio-maxlen)]}]]
            [:div.col-xs-12
             [:div.row.col-xs-12.mar-top-20
              [:div.col-xs-12.col-sm-6.col-sm-offset-6
               [ui/flat-button {:label        "Save"
                                :primary      true
                                :disabled     has-err?
                                :on-touch-tap #(f/dispatch [:edit-profile])}]]]]]))])))
