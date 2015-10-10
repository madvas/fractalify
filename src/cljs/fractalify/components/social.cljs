(ns fractalify.components.social
  (:require [reagent.core :as r]))


(defn create-class [class-name]
  (r/adapt-react-class (aget js/ReactSocial class-name)))

(def fb-button (create-class "FacebookButton"))
(def twitter-button (create-class "TwitterButton"))
(def pinterest-button (create-class "PinterestButton"))
(def vk-button (create-class "VKontakteButton"))
