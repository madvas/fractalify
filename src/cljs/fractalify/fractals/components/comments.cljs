(ns fractalify.fractals.components.comments
  (:require [re-frame.core :as f]
            [material-ui.core :as ui]
            [fractalify.styles :as y]
            [fractalify.utils :as u]
            [plumbing.core :as p]
            [reagent.core :as r]
            [fractalify.router :as t]
            [cljs-time.core :as m]
            [fractalify.components.icon-button-remove :as icon-button-remove]
            [fractalify.components.api-wrap :as api-wrap]))


(def comments-api-wrap
  (api-wrap/create-api-wrap
    :fractal-comments
    :fractal-comments
    [:fractals :fractal-detail :comments]
    :route-params))

(defn comments-content []
  (let [logged-user (f/subscribe [:logged-user])]
    (fn [comments]
      [ui/paper
       [ui/list
        {:style     y/mar-top-10
         :subheader "Comments"}
        (doall
          (for [comment comments]
            (p/letk [[id text datetime [:author gravatar username]] comment]
              ^{:key id}
              [ui/list-item
               {:left-avatar       (r/as-element [:a {:href (t/url :user-view :username username)}
                                                  [ui/avatar {:src gravatar}]])
                :disabled          true
                :right-icon-button (when (= (:username @logged-user) username)
                                     (r/as-element (icon-button-remove/icon-button-remove
                                                     {:on-touch-tap #(f/dispatch [:remove-comment id])})))
                :secondary-text    (u/time-ago datetime)}
               [:p text]])))]])))

(defn comments []
  [comments-api-wrap
   [comments-content]])