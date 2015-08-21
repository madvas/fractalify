(ns fractalify.example.views
  (:require [re-frame.core :as rf]
            [reagent.core :as r]
            [material-ui.core :as ui]
            [fractalify.utils :as u]
            [fractalify.main.views :refer [panels]]))

(defn login-panel []
  [:div "this is login page"])

(defmethod panels :login [] [login-panel])


