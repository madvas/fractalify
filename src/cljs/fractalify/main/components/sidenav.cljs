(ns fractalify.main.components.sidenav
  (:require
    [re-frame.core :as f]
    [reagent.core :as r]
    [material-ui.core :as ui]
    [fractalify.utils :as u]
    [fractalify.router :as t]))

(def ^:dynamic *sidenav-parent* (atom))

(defn- get-sidenav-ref []
  (aget @*sidenav-parent* "refs" "sidenav"))

(defn toggle-sidenav! []
  (when @*sidenav-parent*
    (.toggle (get-sidenav-ref))))

(defn close-sidenav! []
  (when @*sidenav-parent*
    (.close (get-sidenav-ref))))

(defn sidenav []
  (let [logged-user (f/subscribe [:logged-user])]
    (r/create-class
      {:component-did-mount
       (fn [this]
         (reset! *sidenav-parent* this))
       :reagent-render
       (fn []
         [ui/left-nav
          {:ref       "sidenav"
           :docked    false
           :on-change (fn [_ _ menu-item]
                        (when (= "Logout" (:text (js->clj menu-item :keywordize-keys true)))
                          (f/dispatch [:logout])))
           :menuItems [{:text (:username @logged-user)
                        :type ui/MENU-ITEM-SUBHEADER}
                       {:text    "Create Fractal"
                        :payload (t/url :fractal-create)
                        :type    ui/MENU-ITEM-LINK}
                       {:text    "Change Password"
                        :payload (t/url :change-password)
                        :type    ui/MENU-ITEM-LINK}
                       {:text    "Edit profile"
                        :payload (t/url :edit-profile)
                        :type    ui/MENU-ITEM-LINK}
                       {:text "Logout"}]}])})))
