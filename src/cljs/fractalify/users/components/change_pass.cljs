(ns fractalify.users.components.change-pass
  (:require [material-ui.core :as ui]
            [fractalify.validators :as v]
            [fractalify.components.paper-panel :as paper-panel]
            [re-frame.core :as f]
            [fractalify.components.form :as form]
            [fractalify.components.form-text :as form-text]))

(defn change-pass []
  [form/form :users :change-password
   (fn [vals has-err?]
     (let [{:keys [current-pass new-pass confirm-new-pass]} vals]
       [paper-panel/paper-panel
        [:div.col-xs-12
         [:h1 "Change Password"]]
        [:div.col-xs-12
         [form-text/password current-pass "Current password"
          [:users :change-password :current-pass]]
         [form-text/password new-pass "New password"
          [:users :change-password :new-pass]]
         [form-text/password confirm-new-pass "Confirm new password"
          [:users :change-password :confirm-new-pass]
          {:validators [(v/passwords-match new-pass)]}]
         [:div.row.col-xs-12.mar-top-20
          [:div.col-xs-12.col-sm-6.col-sm-offset-6
           [ui/flat-button {:label      "Save"
                            :primary    true
                            :disabled   has-err?
                            :onTouchTap #(f/dispatch [:change-password])}]]]]]))])
