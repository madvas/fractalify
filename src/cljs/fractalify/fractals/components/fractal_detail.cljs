(ns fractalify.fractals.components.fractal-detail
  (:require [fractalify.fractals.components.canvas-controls :as canvas-controls]
            [fractalify.fractals.components.fractal-page-layout :as fractal-page-layout]
            [re-frame.core :as f]
            [plumbing.core :as p]
            [fractalify.utils :as u]
            [fractalify.styles :as y]
            [fractalify.fractals.schemas :as ch]
            [schema.core :as s :include-macros true]
            [material-ui.core :as ui]
            [fractalify.router :as t]
            [reagent.core :as r]
            [fractalify.db-utils :as d]
            [fractalify.fractals.components.comments :as comments]))

(s/set-fn-validation! true)

(defn canvas-section [_]
  (p/fnk [src] :- ch/PublishedFractal
    [:img {:src   src
           :style y/w-100}]))

(defn btn-section [_]
  (p/fnk
    [[:info title {desc ""}]
     star-count
     starred-by-me
     [:author gravatar username]] :- ch/PublishedFractal
    [ui/paper {:style y/paper-block}
     [:div.row.between-xs.middle-xs
      [:div.col-xs
       [:h1.mar-bot-10 title]
       [:div.row.center-xs
        [:a.col-xs-2.mar-bot-10.default-color
         {:href (t/url :user-view :username username)}
         [ui/avatar {:src gravatar}]
         [:h6.mar-top-5 username]]
        [:div.col-xs.text-left
         [:h3 desc]]]]
      [:div.row.middle-xs.col-xs-4
       [:div.col-xs-6.text-right
        [:h1 star-count]]
       [:div.col-xs-6
        [:div
         [ui/floating-action-button
          {:icon-class-name  (str "mdi mdi-star" (when-not starred-by-me "-outline"))
           :background-color (ui/color :green400)
           :on-touch-tap     #(f/dispatch [:fractal-toggle-star])}]]]]]]))

(defn sidebar-section []
  [:a {:href (str "/fractals/" (rand-int 5000))} "Click here"])

(defn fractal-detail []
  (let [fractal (f/subscribe [:fractal-detail-query])]
    (fn []
      [fractal-page-layout/fractal-page-layout
       (when-not (d/empty? @fractal) [canvas-section @fractal])
       (when-not (d/empty? @fractal)
         [:div
          [btn-section @fractal]
          [comments/comments]])
       [sidebar-section]])))