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
            [fractalify.components.form-text :as form-text]))

(def fractals-api-wrap
  (api-wrap/create-api-wrap
    {:endpoint-key     :fractals
     :path             [:fractals :fractals-sidebar]
     :value-sub        :fractals-sidebar
     :query-params-sub :fractals-sidebar-query-params}))

(def order-items
  [{:payload :best :text "Top Rated"}
   {:payload :recent :text "Most Recent"}
   {:payload :random :text "Random"}])

(defn list-order-select []
  [form-select/form-select [:fractals :sidebar :order]
   {:menu-items order-items}])

(def pagination-items
  [["rewind" "First page" #(identity 1)]
   ["chevron-double-left" "Back 10 pages" #(max (- %1 10) %2)]
   ["chevron-left" "Previous page" #(max (dec %1) 1)]
   ["chevron-right" "Next page" #(min (inc %1) %2)]
   ["chevron-double-right" "Forward 10 pages" #(min (+ %1 10) %2)]
   ["fast-forward" "Last Page" #(identity %2)]])

(defn pagination [_]
  (let [query-params (f/subscribe [:fractals-sidebar-query-params])]
    (fn [fractals loading?]
      (when fractals
        (p/letk [[page limit] @query-params
                 [total-items] fractals]
          [:div
           [:div.row.around-xs.middle-xs
            (for [[icon tooltip] pagination-items]
              ^{:key icon}
              [ui/icon-button
               {:icon-class-name (str "mdi mdi-" icon)
                :icon-style      {:color (ui/color :grey700)}
                :tooltip         tooltip}])]
           [:div.row.center-xs
            (str page "/" total-items)]])))))

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
                  [ui/font-icon {:style      {:font-size "1.2em"}
                                 :class-name "mdi mdi-star"}]
                  [:h6 star-count]]]]
               [ui/list-divider]]))))]
     [pagination fractals loading?]]))

(defn fractals-sidebar []
  (fn []
    [ui/paper {:style y/sidebar-wrap}
     [list-order-select]
     [fractals-api-wrap
      [fractal-list]]]))
