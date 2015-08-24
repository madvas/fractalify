(ns fractalify.main.components.about
  (:require [fractalify.router :as t]))

(defn about []
  (fn []
    [:div "This is thee About Page."
     [:div [:a {:href (t/url :home)} "go to Home Page"]]]))