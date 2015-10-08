(ns fractalify.fractals.components.fractal-card-list
  (:require [fractalify.utils :as u]
            [fractalify.fractals.components.star-count :as star-count]
            [fractalify.styles :as y]
            [material-ui.core :as ui]
            [fractalify.router :as t]
            [plumbing.core :as p]
            [schema.core :as s :include-macros true]
            [fractalify.fractals.schemas :as fch]
            [reagent.core :as r]))

(def url (partial t/url :fractal-detail :id))

(defn frac-link [id children]
  [:a.block.default-color.no-dec.text-left
   {:href (t/url :fractal-detail :id id)}
   children])

(s/defn fractal-card-list [fractals :- (s/maybe fch/PublishedFractalsList) props]
  [:div (r/merge-props {:class "row"} (:container-props props))
   (when fractals
     (doall
       (for [fractal (:items fractals)]
         (p/letk [[id src title star-count created] fractal]
           ^{:key id}
           [:div
            (r/merge-props {:class "col-xs-6 col-sm-4 col-md-3 col-lg-3 mar-bot-20"} props)
            [ui/card
             [frac-link id
              [ui/card-media
               [:img {:src src}]]]
             [frac-link id
              [ui/card-text {:style (merge y/pad-hor-10 y/pad-ver-5)}
               [:div.row.mar-bot-5
                [:div.col-xs-12 title]]
               [:div.row.between-xs.middle-xs
                [:div.col-xs-6
                 [star-count/star-count star-count]]
                [:h5.col-xs-6.text-right
                 (u/time-ago created)]]]]
             (when-let [actions (:actions props)]
               [ui/card-actions
                (conj actions fractal)])]]))))])