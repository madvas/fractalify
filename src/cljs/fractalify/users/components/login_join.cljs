(ns fractalify.users.components.login-join
  (:require [re-frame.core :as f]
            [material-ui.core :as ui]
            [fractalify.styles :as y]
            [fractalify.router :as t]
            [fractalify.components.form-input :as form-input]
            [schema.core :as s :include-macros true]
            [fractalify.components.tab-anchor :as tab-anchor]
            [fractalify.validators :as v]
            [fractalify.components.responsive-panel :as responsive-panel]
            [fractalify.components.paper-content :as paper-content]
            [fractalify.components.form :as form]
            [plumbing.core :as p]
            [fractalify.utils :as u]))

(defn login-tab []
  [form/form :users :login
   (fn [vals has-err?]
     (let [{:keys [user password]} vals]
       [paper-content/paper-content
        [:div.col-xs-12
         [form-input/text user "Username or email" [:users :login :user]
          {:required true}]]

        [:div.col-xs-12
         [form-input/password "Password" password [:users :login :password]]]

        [:div.row.col-xs-12.mar-top-20
         [:div.col-xs-6
          [ui/flat-button {:label      "Forgot password?"
                           :linkButton true
                           :href       (t/url :forgot-password)
                           :tab-index  -1
                           :style      y/text-center}]]
         [:div.col-xs-6
          [ui/flat-button {:label      "Login"
                           :primary    true
                           :disabled   has-err?
                           :onTouchTap #(f/dispatch [:login])}]]]]))])

(defn join-tab []
  [form/form :users :join
   (fn [vals has-err?]
     (let [{:keys [username email password confirm-pass]} vals]
       [paper-content/paper-content
        [:div.col-xs-12
         [form-input/text username "Username" [:users :join :username]
          {:required   true
           :validators [(v/length 3)]}]]

        [:div.col-xs-12
         [form-input/email email "Email" [:users :join :email]]]

        [:div.col-xs-12
         [form-input/password password "Password" [:users :join :password]]]

        [:div.col-xs-12
         [form-input/password confirm-pass "Confirm Password" [:users :join :confirm-pass]
          {:validators [(v/passwords-match password)]}]]

        [:div.row.col-xs-12.mar-top-20
         [:div.col-xs-12.col-sm-6.col-sm-offset-6
          [ui/flat-button {:label      "Create Account"
                           :primary    true
                           :disabled   has-err?
                           :onTouchTap #(f/dispatch [:join])}]]]]))])

(s/defn login-join [type :- (s/enum :login :join)]
  [responsive-panel/responsive-panel
   [ui/tabs {:initialSelectedIndex (if (= type :login) 0 1)}
    (tab-anchor/tab-anchor {:label "Login" :href (t/url :login)} [login-tab])
    (tab-anchor/tab-anchor {:label "Join" :href (t/url :join)} [join-tab])]])

