(ns fractalify.users.components.login-join
  (:require [re-frame.core :as f]
            [material-ui.core :as ui]
            [fractalify.styles :as y]
            [fractalify.router :as t]
            [fractalify.components.form-text :as form-text]
            [schema.core :as s :include-macros true]
            [fractalify.components.tab-anchor :as tab-anchor]
            [fractalify.validators :as v]
            [fractalify.components.responsive-panel :as responsive-panel]
            [fractalify.components.paper-content :as paper-content]
            [fractalify.components.password :as password]
            [fractalify.components.email :as email]))

(defn login-tab []
  (let [form-errors (f/subscribe [:users-form-errors :login])]
    (fn []
      [paper-content/paper-content
       [:div.col-xs-12
        [form-text/form-text [:users :login :user]
         {:floating-label-text "Username or email"
          :required            true}]]

       [:div.col-xs-12
        [password/password "Password" [:users :login :password]]]

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
                          :disabled   (not (empty? @form-errors))
                          :onTouchTap #(f/dispatch [:login])}]]]])))

(defn join-tab []
  (let [form-errors (f/subscribe [:users-form-errors :join])
        password (f/subscribe [:form-item :users :join :password])]
    (fn []
      [paper-content/paper-content
       [:div.col-xs-12
        [form-text/form-text [:users :join :username]
         {:floating-label-text "Username"
          :required            true
          :validators          [(partial v/length 3)]}]]

       [:div.col-xs-12
        [email/email [:users :join :email]]]

       [:div.col-xs-12
        [password/password "Password" [:users :join :password]]]

       [:div.col-xs-12
        [password/password "Confirm Password" [:users :join :confirm-pass]
         {:validators [#(v/passwords-match % @password)]}]]

       [:div.row.col-xs-12.mar-top-20
        [:div.col-xs-12.col-sm-6.col-sm-offset-6
         [ui/flat-button {:label      "Create Account"
                          :primary    true
                          :disabled   (not (empty? @form-errors))
                          :onTouchTap #(f/dispatch [:join])}]]]])))

(s/defn login-join [type :- (s/enum :login :join)]
  [responsive-panel/responsive-panel
   [ui/tabs {:initialSelectedIndex (if (= type :login) 0 1)}
    (tab-anchor/tab-anchor {:label "Login" :href (t/url :login)} [login-tab])
    (tab-anchor/tab-anchor {:label "Join" :href (t/url :join)} [join-tab])]])

