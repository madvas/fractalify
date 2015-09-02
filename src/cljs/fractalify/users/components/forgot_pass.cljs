(ns fractalify.users.components.forgot-pass
  (:require [fractalify.components.paper-panel :as paper-panel]
            [fractalify.validators :as v]
            [fractalify.components.form-text :as form-text]
            [re-frame.core :as f]
            [material-ui.core :as ui]))


(defn forgot-pass []
  (let [form-errors (f/subscribe [:form-errors :forgot-password])]
    (fn []
      [paper-panel/paper-panel
       [:div.col-xs-12
        [:h1 "Restore Password"]]
       [:div.col-xs-12
        [form-text/form-text [:forgot-password :email]
         {:floating-label-text "Your email"
          :required            true
          :validators          [v/email]}]
        [:div.row.col-xs-12.mar-top-20
         [:div.col-xs-12.col-sm-6.col-sm-offset-6
          [ui/flat-button {:label      "Send"
                           :primary    true
                           :disabled   (not (empty? @form-errors))
                           :onTouchTap #(f/dispatch [:forgot-password])}]]]]])))
