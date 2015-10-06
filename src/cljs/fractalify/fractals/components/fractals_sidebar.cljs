(ns fractalify.fractals.components.fractals-sidebar
  (:require [material-ui.core :as ui]
            [fractalify.styles :as y]
            [fractalify.components.api-wrap :as api-wrap]
            [plumbing.core :as p]
            [reagent.core :as r]
            [fractalify.utils :as u]
            [fractalify.router :as t]
            [re-frame.core :as f]
            [fractalify.components.form-select :as form-select]
            [fractalify.fractals.components.sidebar-pagination :as sidebar-pagination]
            [fractalify.components.form :as form]
            [fractalify.fractals.components.star-count :as star-count]))

(def fractals-api-wrap
  (api-wrap/create-api-wrap
    {:api-route        :fractals
     :path             [:fractals :fractals-sidebar]
     :value-sub        :fractals-sidebar
     :query-params-sub :fractals-sidebar-query-params}))

(def sort-items
  [{:payload :best :text "Top Rated"}
   {:payload :recent :text "Most Recent"}])

(defn list-order-select []
  [form/form :fractals :sidebar
   (fn [vals]
     [form-select/form-select (:sort vals) "Order by" [:fractals :sidebar :sort]
      {:menu-items sort-items}])])

(defn fractal-list []
  (fn [fractals loading?]
    [:div
     [ui/list {:style y/pad-0}
      (cond
        loading? [ui/linear-progress {:mode "indeterminate"}]
        (u/empty-seq? fractals) [ui/list-item {:disabled true} "No fractals found"]
        :else
        (doall
          (for [fractal (:items fractals)]
            (p/letk [[id title desc star-count src] fractal]
              ^{:key id}
              [:div
               [ui/list-item
                {:on-touch-tap #(f/dispatch [:fractals-sidebar-select fractal])}
                [:div.row.middle-xs
                 [:div.col-xs-2
                  [ui/avatar {:src   src
                              :style {:border-radius 0}
                              :size  60}]]
                 [:div.col-xs-9.mar-lef-5
                  [:div.col-xs-12 title]
                  [:h6.col-xs-12.mar-top-5 (u/ellipsis desc 20)]]
                 [:div.col-xs-1.row.center-xs
                  [star-count/star-count star-count]]]]
               [ui/list-divider]]))))]
     [sidebar-pagination/sidebar-pagination fractals loading?]]))

(defn fractals-sidebar []
  (fn []
    [ui/paper {:style y/sidebar-wrap}
     [list-order-select]
     [fractals-api-wrap
      [fractal-list]]]))
