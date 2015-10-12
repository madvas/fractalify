(ns fractalify.main.components.contact
  (:require [fractalify.components.form-input :as form-input]
            [fractalify.components.paper-content :as paper-content]
            [material-ui.core :as ui]
            [re-frame.core :as f]
            [fractalify.components.form :as form]
            [fractalify.components.paper-panel :as paper-panel]))

(defn contact []
  (let [logged-user (f/subscribe [:logged-user])]
    (when-let [email (:email @logged-user)]
      (f/dispatch-sync [:set-form-item :main :contact :email email]))
    [form/form :main :contact
     (fn [vals has-err?]
       (let [{:keys [email subject text]} vals]
         [paper-panel/paper-panel
          [:h1.w-100.text-center "Contact Us"]

          [form-input/email email "Email" [:main :contact :email]
           {:name :email}]

          [form-input/text subject "Subject" [:main :contact :subject]
           {:required true}]

          [form-input/text text "Message" [:main :contact :text]
           {:required   true
            :multi-line true}]

          [:div.row.col-xs-12.mar-top-20
           [:div.col-xs-12.col-sm-6.col-sm-offset-6
            [ui/flat-button {:label      "Send"
                             :primary    true
                             :type       :submit
                             :disabled   has-err?
                             :onTouchTap #(f/dispatch [:contact])}]]]]))]))