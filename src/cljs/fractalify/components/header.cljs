(ns fractalify.components.header
  (:require
    [re-frame.core :as f]
    [reagent.core :as r]
    [material-ui.core :as ui]
    [fractalify.components.sidenav :as sidenav]))

(defn- right-button [label href]
  [ui/flat-button {:label      label
                   :linkButton true
                   :href       href}])

(defn header []
  (let [user (f/subscribe [:user])]
    (fn []
      (let [right-btn (if @user
                        (right-button (:username @user) (str "#/user/" (:username @user)))
                        (right-button "Login / Join" "#/login"))]
        [ui/app-bar {
                     :title                    "Fractalify"
                     :iconElementRight         (r/as-element right-btn)
                     :showMenuIconButton       (not (empty? @user))
                     :onLeftIconButtonTouchTap #(sidenav/toggle-sidenav)}]))))
