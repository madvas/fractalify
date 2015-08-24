(ns fractalify.main.components.footer
  (:require
    [re-frame.core :as f]
    [reagent.core :as r]
    [material-ui.core :as ui]
    [fractalify.router :as t]
    [fractalify.styles :as y]))

(defn footer []
  [:footer
   [:div
    [:a {:href (t/url :about)} "About"]
    [:br]
    [:a {:href (t/url :change-password)} "Change password"]]])

