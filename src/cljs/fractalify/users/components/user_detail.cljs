(ns fractalify.users.components.user-detail
  (:require [fractalify.components.api-wrap :as api-wrap]
            [plumbing.core :as p]
            [fractalify.users.schemas :as uch]
            [schema.core :as s :include-macros true]
            [material-ui.core :as ui]
            [fractalify.components.gravatar :as gravatar]
            [fractalify.fractals.schemas :as fch]
            [fractalify.utils :as u]
            [fractalify.styles :as y]
            [fractalify.router :as t]
            [fractalify.fractals.components.fractal-card-list :as fractal-card-list]
            [re-frame.core :as f]))

(def user-api-wrap
  (api-wrap/create-api-wrap
    {:endpoint-key     :user
     :path             [:users :user-detail]
     :value-sub        :user-detail
     :query-params-sub :route-params}))

(def user-fractals-api-wrap
  (api-wrap/create-api-wrap
    {:endpoint-key     :fractals
     :path             [:fractals :fractals-user]
     :value-sub        :fractals-user
     :query-params-sub :fractals-user-query-params
     :force-reload     true}))

(def gravatar-size 300)

(defn user-card []
  (s/fn [user :- (s/maybe uch/User)]
    [:div.row.center-xs.col-xs-12.col-lg-3.top-xs.text-left
     (when user
       (p/letk [[username gravatar bio] user]
         [ui/card {:style {:max-width gravatar-size}}
          [ui/card-media
           [gravatar/gravatar gravatar gravatar-size]]
          [ui/card-title [:h2 username]]
          [ui/card-text [:div bio]]]))]))

(defn remove-fractal-btn []
  (s/fn [fractal :- fch/PublishedFractal]
    [:div.col-xs-12.mar-0
     [ui/flat-button
      {:label        "Delete"
       :on-touch-tap #(f/dispatch [:fractal-remove fractal])}]]))

(defn user-fractals []
  (let [logged-user (f/subscribe [:logged-user])]
    (s/fn [fractals :- (s/maybe fch/PublishedFractalsList)]
      [:div.row.col-xs-12.col-lg-9.top-xs
       [:div.row.col-xs-12.text-left.pad-hor-20.center-xs
        [:h1.col-xs-12.pad-top-10 (str "Fractals (" (:total-items fractals) ")")]
        [:hr.col-xs-12]]
       (if (u/empty-seq? (:items fractals))
         [:h3.col-xs-12 "This user haven't created anything yet"]
         [:div.row.col-xs-12.text-left.pad-hor-20.center-xs
          [fractal-card-list/fractal-card-list fractals
           {:actions [remove-fractal-btn]}]])])))

(defn user-detail []
  [:div.row.center-xs
   [user-api-wrap
    [user-card]]
   [user-fractals-api-wrap
    [user-fractals]]])
