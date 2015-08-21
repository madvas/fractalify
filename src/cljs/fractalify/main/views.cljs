(ns fractalify.main.views
  (:require [re-frame.core :as f]
            [reagent.core :as r]
            [material-ui.core :as ui]
            [fractalify.utils :as u]
            [fractalify.components.header :as header]
            [fractalify.components.sidenav :as sidenav]))

;; --------------------

(defn open-dialog [this]
  (.show (.. this -refs -CustomDialog)))

(defn home-panel []
  [:p "This is home"])


#_[:div (str "Hello fromaaa " @name ". This is the Home Page.")
   [:div [:a {:href "#/about"} "goo to About Page"]]
   [ui/text-field {:hintText          "Please enter your first name"
                   :floatingLabelText "First Name"}]
   [ui/flat-button {:label "Materr"}]
   [ui/avatar {:src "http://material-ui.com/images/uxceo-128.jpg"}]
   [ui/icon-button {:iconClassName   "mdi mdi-bell"
                    :tooltipPosition "bottom-center"
                    :tooltip         "Sky"}]
   [ui/date-picker {:hintText "Here Date"}]
   [ui/raised-button {:label "Open Dialog" :onTouchTap #(open-dialog this)}]
   [ui/dialog {:ref "CustomDialog" :title "This is a Dialog"} "Here goes simething nice"]
   [ui/drop-down-menu {:menuItems [{:payload 1 :text "Never"} {:payload 2 :text "Every"}]}]
   [ui/left-nav {:docked false :ref "leftNav" :menuItems [{:route "get-started" :text "Get Started"}]}]
   [ui/raised-button {:label "Open Sidenav" :onTouchTap #(open-sidenav this)}]
   [ui/font-icon {:className "mdi mdi-bell"}]
   [ui/paper {:zDepth 5 :circle true} [:p "This is circle"]]
   [ui/list
    [ui/list-item {:primaryText "Inbox" :leftIcon (r/as-element [ui/font-icon {:className "mdi mdi-bell"}])}]]
   [ui/linear-progress {:mode "determinate" :value 60}]
   [ui/circular-progress {:mode "determinate" :value 60}]
   [ui/checkbox {:name "checkboxname1" :value "val" :label "went for a run"}]
   [ui/tabs

    [ui/tab {:label "Item One"} [:div "here ok"]]
    [ui/tab {:label "Item Two"} [:div "here no ok"]]]
   [ui/text-field {:hintText          "Hint text"
                   :errorText         "Bad bad"
                   :floatingLabelText "I go up"}]]

(defn about-panel []
  (fn []
    [:div "This is thee About Page."
     [:div [:a {:href "#/"} "go to Home Page"]]]))


;; --------------------
(defmulti panels identity)
(defmethod panels :home [] [home-panel])
(defmethod panels :about [] [about-panel])
(defmethod panels :default [] [:p "Page not found"])

(def ^:dynamic *mui-theme*
  (.getCurrentTheme (js/MaterialUI.Styles.ThemeManager.)))

(defn main-panel []
  (let [active-panel (f/subscribe [:active-panel])]
    (r/create-class
      {:display-name "Main Panel"

       :child-context-types
                     #js {:muiTheme js/React.PropTypes.object}

       :get-child-context
                     (fn [_]
                       #js {:muiTheme *mui-theme*})
       :reagent-render
                     (fn []
                       [:div
                        [header/header]
                        [sidenav/sidenav]
                        (panels @active-panel)])})))


