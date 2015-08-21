(ns fractalify.auth.views
  (:require
    [re-frame.core :as f]
    [reagent.core :as r]
    [material-ui.core :as ui]
    [fractalify.utils :as u]
    [fractalify.styles :as y]
    [fractalify.main.views :refer [panels]]
    [schema.core :as s :include-macros true]
    [fractalify.components.tab-anchor :as tab-anchor]
    [fractalify.components.text-field :as text-field]
    [fractalify.validators :as v]))

(defn tab-content [& children]
  [ui/paper
   {:style y/pad-20}
   (into [:div.row] children)])

(defn login-tab []
  (let [form-errors (f/subscribe [:form-errors :login])]
    (fn []
      [tab-content
       [:div.col-xs-12 [text-field/text-field :login-user [:login :user]
                        {:floatingLabelText "Username or email"
                         :required          true}]]

       [:div.col-xs-12 [text-field/text-field :login-password [:login :password]
                        {:floatingLabelText "Password"
                         :type              "password"
                         :required          true}]]

       [:div.row.col-xs-12.mar-top-20
        [:div.col-xs-6
         [ui/flat-button {:label      "Forgot password?"
                          :linkButton true
                          :href       "#/forgot-password"
                          :tab-index  -1
                          :style      y/text-center}]]
        [:div.col-xs-6
         [ui/flat-button {:label      "Login"
                          :primary    true
                          :disabled   (not (empty? @form-errors))
                          :onTouchTap #(f/dispatch [:login])}]]]])))

(defn join-tab []
  [tab-content "Join here"])

(def tabs [{:label "Login" :href "#/login" :content login-tab}
           {:label "Join" :href "#/join" :content join-tab}])

(s/defn auth-panel [type :- (s/enum :login :join)]
  [:div.row.middle-xs.center-xs
   [:div.col-xs-12.col-sm-8.col-md-6.col-lg-4
    [ui/tabs {:initialSelectedIndex (if (= type :login) 0 1)}
     (for [tab tabs]
       (tab-anchor/tab-anchor
         (select-keys tab [:label :href])
         [(:content tab)]))]]])

(defmethod panels :login [] [(partial auth-panel :login)])
(defmethod panels :join [] [(partial auth-panel :join)])



