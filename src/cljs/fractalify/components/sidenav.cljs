(ns fractalify.components.sidenav
  (:require
    [re-frame.core :as f]
    [reagent.core :as r]
    [material-ui.core :as ui]
    [fractalify.utils :as u]))

(def sidenav-parent (atom))

(defn- get-sidenav-ref []
  (.. @sidenav-parent -refs -sidenav))

(defn toggle-sidenav []
  (.toggle (get-sidenav-ref)))

(defn sidenav []
  (r/create-class
    {:component-did-mount (fn [this]
                            (reset! sidenav-parent this))
     :reagent-render
                          (fn []
                            [ui/left-nav {:ref       "sidenav"
                                          :docked    false
                                          :menuItems [{:text    "First item"
                                                       :payload "#/hello"
                                                       :type    (.. js/MaterialUI -MenuItem -Types -LINK)}]}])}))
