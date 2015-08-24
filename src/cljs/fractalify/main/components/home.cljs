(ns fractalify.main.components.home
  (:require [fractalify.router :as t]))

(defn home []
  [:p "This is home "])


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