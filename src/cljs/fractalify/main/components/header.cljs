(ns fractalify.main.components.header
  (:require
    [re-frame.core :as f]
    [reagent.core :as r]
    [material-ui.core :as ui]
    [fractalify.main.components.sidenav :as sidenav]
    [fractalify.router :as t]
    [fractalify.styles :as y]
    [fractalify.utils :as u]))

(defn- right-button
  ([label href] (right-button label href {}))
  ([label href props] (right-button label href props ui/flat-button))
  ([label href props btn-fn]
   [btn-fn (r/merge-props
             {:label      label
              :linkButton true
              :href       href
              :style      y/header-btn} props)]))

(defn header []
  (let [user (f/subscribe [:logged-user])]
    (fn []
      (let [right-btn (if @user
                        [:div.row.middle-xs.pad-top-5
                         [:div.col-xs
                          (right-button "Create"
                                        (t/url :fractal-create)
                                        {:primary true
                                         }
                                        ui/raised-button)]
                         [:div.col-xs
                          (right-button (:username @user)
                                        (t/url :user-detail :username (:username @user)))]]
                        (right-button "Login / Join"
                                      (t/url :login)))]
        [ui/app-bar {:title                    (r/as-element
                                                 [:a {:href (t/url :home) :class "no-dec"}
                                                  [:img {:src   "public/img/fractalify.svg"
                                                         :style y/logo}]])
                     :iconElementRight         (r/as-element right-btn)
                     :showMenuIconButton       (not (empty? @user))
                     :onLeftIconButtonTouchTap #(sidenav/toggle-sidenav!)}]))))
