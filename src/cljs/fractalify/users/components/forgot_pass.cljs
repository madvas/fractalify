(ns fractalify.users.components.forgot-pass
  (:require [fractalify.components.paper-panel :as paper-panel]
            [fractalify.validators :as v]
            [fractalify.components.form-input :as form-input]
            [re-frame.core :as f]
            [material-ui.core :as ui]
            [fractalify.components.form :as form]))


(defn forgot-pass []
  [form/form :users :forgot-password
   (fn [vals has-err?]
     [paper-panel/paper-panel
      [:div.col-xs-12
       [:h1 "Restore Password"]]
      [:div.col-xs-12
       [form-input/email (:email vals) "Your Email" [:users :forgot-password :email]]
       [:div.row.col-xs-12.mar-top-20
        [:div.col-xs-12.col-sm-6.col-sm-offset-6
         [ui/flat-button {:label      "Send"
                          :primary    true
                          :disabled   has-err?
                          :onTouchTap #(f/dispatch [:forgot-password])}]]]]])])
