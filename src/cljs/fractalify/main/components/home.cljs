(ns fractalify.main.components.home
  (:require [fractalify.router :as t]
            [material-ui.core :as ui]
            [fractalify.components.api-wrap :as api-wrap]
            [schema.core :as s :include-macros true]
            [fractalify.fractals.schemas :as fch]
            [plumbing.core :as p]
            [fractalify.utils :as u]
            [fractalify.styles :as y]
            [fractalify.fractals.components.fractal-card-list :as fractal-card-list]))

(s/defn create-home-api [type :- fch/FractalOrderTypes]
  (api-wrap/create-api-wrap
    {:endpoint-key     :fractals
     :path             [:fractals :fractals-home type]
     :value-sub        [:fractals-home type]
     :query-params-sub [:fractals-home-query-params type]
     :force-reload     true}))

(def best-api-wrap (create-home-api :best))
(def recent-api-wrap (create-home-api :recent))

(defn fractals-list [_]
  (fn [title fractals]
    [:div.row.center-xs.mar-bot-20
     [:h1.col-xs-12 title]
     [:hr.col-xs-10.mar-bot-20]
     [fractal-card-list/fractal-card-list fractals {:class           "col-lg-2p5"
                                                    :container-props {:class "center-xs"}}]]))

(defn home []
  [:div.pad-20
   [best-api-wrap
    [fractals-list "Top Rated"]]
   [recent-api-wrap
    [fractals-list "Most Recent"]]])