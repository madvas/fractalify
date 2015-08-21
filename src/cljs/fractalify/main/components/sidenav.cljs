(ns fractalify.main.components.sidenav
  (:import goog.History)
  (:require
    [re-frame.core :as f]
    [reagent.core :as r]
    [material-ui.core :as ui]
    [fractalify.utils :as u]))

(def ^:dynamic *sidenav-parent* (atom))

(defn- get-sidenav-ref []
  (.. @*sidenav-parent* -refs -sidenav))

(defn toggle-sidenav! []
  (when @*sidenav-parent*
    (.toggle (get-sidenav-ref))))

(defn close-sidenav! []
  (when @*sidenav-parent*
    (.close (get-sidenav-ref))))

(defn sidenav []
  (r/create-class
    {:component-did-mount (fn [this]
                            (reset! *sidenav-parent* this))
     :reagent-render
                          (fn []
                            [ui/left-nav {:ref       "sidenav"
                                          :docked    false
                                          :menuItems [{:text    "Logout"
                                                       :payload "#/logout"
                                                       :type    ui/menu-item-link-type}]}])}))
