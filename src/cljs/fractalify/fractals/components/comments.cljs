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
            [fractalify.components.api-wrap :as api-wrap]
            [fractalify.components.form-input :as form-input]
            [fractalify.components.form :as form]))


(def comments-api-wrap
  (api-wrap/create-api-wrap
    {:api-route         :fractal-comments
     :path              [:fractals :fractal-detail :comments]
     :value-sub         :fractal-comments
     :query-params-sub  :route-params
     :route-param-names [:id]}))

(defn add-comment [_]
  (fn [logged-user fractal]
    (when logged-user
      [form/form :fractals :comment
       (fn [vals has-err?]
         [:div.row.pad-hor-10.mar-top-10.end-xs
          [:div.col-xs-12
           [form-input/text (:text vals) "Enter your comment" [:fractals :comment :text]
            {:required   true
             :multi-line true}]]
          [:div.col-xs-3
           [ui/flat-button
            {:label        "Send"
             :on-touch-tap #(f/dispatch [:fractal-comment-add (:id fractal)])
             :disabled     has-err?}]]])])))

(defn comment-list [logged-user fractal comments-list loading?]
  (let [comments (:items comments-list)]
    [ui/list
     {:style     y/mar-top-10
      :subheader "Comments"}
     (cond
       loading? [ui/linear-progress {:mode "indeterminate"}]
       (u/empty-seq? comments) [ui/list-item {:disabled true} "No comments were added yet"]
       :else
       (doall
         (for [comment comments]
           (p/letk [[id text created [:author gravatar username]] comment]
             ^{:key id}
             [ui/list-item
              {:left-avatar       (r/as-element [:a {:href (t/url :user-detail :username username)}
                                                 [ui/avatar {:src (u/gravatar-url gravatar 50)}]])
               :disabled          true
               :right-icon-button (when (= (:username logged-user) username)
                                    (r/as-element (icon-button-remove/icon-button-remove
                                                    {:on-touch-tap
                                                     #(f/dispatch
                                                       [:fractal-comment-remove
                                                        (:id fractal)
                                                        (:id comment)])})))
               :secondary-text    (u/time-ago created)}
              [:p text]]))))]))

(defn comments []
  (let [logged-user (f/subscribe [:logged-user])]
    (fn [fractal]
      [ui/paper {:style y/comments-wrap}
       [add-comment @logged-user fractal]
       [comments-api-wrap
        [comment-list @logged-user fractal]]])))