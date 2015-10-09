(ns fractalify.users.components.reset-pass
  (:require [material-ui.core :as ui]
            [fractalify.components.paper-panel :as paper-panel]
            [re-frame.core :as f]
            [fractalify.components.form-input :as form-input]
            [fractalify.utils :as u]
            [fractalify.validators :as v]
            [fractalify.components.form :as form]))

(defn reset-pass []
  (let [route-params (f/subscribe [:route-params])]
    (f/dispatch-sync [:set-form :users :reset-password
                      (select-keys @route-params [:username :token]) true])
    (fn []
      [form/form :users :reset-password
       (fn [vals has-err?]
         (let [{:keys [new-pass]} vals]
           [paper-panel/paper-panel
            [:div.col-xs-12
             [:h1 "Reset Password"]]
            [:div.col-xs-12
             [form-input/password new-pass "New password"
              [:users :reset-password :new-pass]]
             [:div.row.col-xs-12.mar-top-20
              [:div.col-xs-12.col-sm-6.col-sm-offset-6
               [ui/flat-button {:label      "Save"
                                :primary    true
                                :disabled   has-err?
                                :onTouchTap #(f/dispatch [:reset-password])}]]]]]))])))
