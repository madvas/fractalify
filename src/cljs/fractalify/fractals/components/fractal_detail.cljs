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
            [fractalify.fractals.components.comments :as comments]
            [fractalify.components.api-wrap :as api-wrap]
            [fractalify.fractals.components.fractals-sidebar :as fractals-sidebar]))

(s/set-fn-validation! true)

(defn canvas-section [_]
  (p/fnk [src] :- ch/PublishedFractal
    [:img {:src   src
           :style y/w-100}]))

(defn btn-section [_]
  (p/fnk
    [id title {desc ""} star-count starred-by-me created
     [:author gravatar username]] :- ch/PublishedFractal
    [ui/paper {:style y/paper-block}
     [:div.row.between-xs.middle-xs
      [:div.col-xs
       [:h1.mar-bot-10 title]
       [:div.row.center-xs
        [:a.col-xs-2.mar-bot-10.default-color
         {:href (t/url :user-detail :username username)}
         [ui/avatar {:src gravatar}]
         [:h6.mar-top-5 username]]
        [:div.col-xs.text-left
         [:h5.mar-bot-5 (u/time-ago created)]
         [:h3 desc]]]]
      [:div.row.middle-xs.col-xs-4
       [:div.col-xs-6.text-right
        [:h1 star-count]]
       [:div.col-xs-6
        [:div
         [ui/floating-action-button
          {:icon-class-name  (str "mdi mdi-star" (when-not starred-by-me "-outline"))
           :background-color (ui/color :green400)
           :on-touch-tap     #(f/dispatch [:fractal-toggle-star id])}]]]]]]))

(defn sidebar-section []
  (fn []
    [ui/tabs
     [ui/tab
      {:label "Other Fractals"}
      [fractals-sidebar/fractals-sidebar]]
     [ui/tab
      {:label "Fractal Settings"}
      [canvas-controls/canvas-controls [:fractal-detail] true]]]))


(defn fractal-detail-content [_]
  (fn [fractal]
    [fractal-page-layout/fractal-page-layout
     (when-not (empty? fractal) [canvas-section fractal])
     (when-not (empty? fractal)
       [:div
        [btn-section fractal]
        [comments/comments]])
     [sidebar-section]]))

(def fractal-api-wrap
  (api-wrap/create-api-wrap
    {:endpoint-key     :fractal
     :path             [:fractals :fractal-detail]
     :value-sub        :fractal-detail
     :query-params-sub :route-params}))

(defn fractal-detail []
  [fractal-api-wrap
   [fractal-detail-content]])