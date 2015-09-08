(ns fractalify.users.components.change-pass
  (:require [material-ui.core :as ui]
            [fractalify.validators :as v]
            [fractalify.components.paper-panel :as paper-panel]
            [re-frame.core :as f]
            [fractalify.components.password :as password]))

(defn change-pass []
  (let [form-errors (f/subscribe [:users-form-errors :change-password])
        new-pass (f/subscribe [:form-item :users :change-password :new-pass])]
    (fn []
      [paper-panel/paper-panel
       [:div.col-xs-12
        [:h1 "Change Password"]]
       [:div.col-xs-12
        [password/password "Current password" [:users :change-password :current-pass]]
        [password/password "New password" [:users :change-password :new-pass]]
        [password/password "Confirm new password" [:users :change-password :confirm-new-pass]
         {:validators [#(v/passwords-match % @new-pass)]}]
        [:div.row.col-xs-12.mar-top-20
         [:div.col-xs-12.col-sm-6.col-sm-offset-6
          [ui/flat-button {:label      "Save"
                           :primary    true
                           :disabled   (not (empty? @form-errors))
                           :onTouchTap #(f/dispatch [:change-password])}]]]]])))
