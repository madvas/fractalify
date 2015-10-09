(ns fractalify.main.components.about
  (:require [fractalify.router :as t]
            [fractalify.components.paper-panel :as paper-panel]))

(defn- a [label href]
  [:a {:href href :target "_blank"}
   label])

(defn about []
  [paper-panel/paper-panel
   [:div.col-xs-12
    [:h1 "About Us"]]
   [:div.col-xs-12.text-left.pad-top-20.line-height-bigger
    "Fractalify is a entertainment and educational webapp for creating & sharing fractal images
     via so called " [a "L-systems" "https://en.wikipedia.org/wiki/L-system"] "."
    [:p.mar-top-10 "Source code for fractalify is fully open-source and can be found on "
     [a "Github" "https://github.com/madvas/fractalify"] "."]
    [:p "The webapp is written purely in " [a "Clojure & Clojurescript" "http://clojure.org/"]
     " and can be freely used for any purpose."]
    [:p.mar-top-20
     "Thank you and enjoy your stay here!"
     [:p "Creator of Fractalify, " [a "madvas" "https://github.com/madvas"] " or "
      [a "@matuslestan" "https://twitter.com/matuslestan"]]]]])