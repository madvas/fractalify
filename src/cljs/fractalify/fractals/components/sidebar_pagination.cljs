(ns fractalify.fractals.components.sidebar-pagination
  (:require [material-ui.core :as ui]
            [plumbing.core :as p]
            [re-frame.core :as f]
            [fractalify.utils :as u]))

(def pagination-items
  [["skip-previous" "First page" #(identity 1)]
   ["chevron-double-left" "Back 10 pages" #(max (- %2 10) 1)]
   ["chevron-left" "Previous page" #(max (dec %2) 1)]
   ["chevron-right" "Next page" #(min (inc %2) %1)]
   ["chevron-double-right" "Forward 10 pages" #(min (+ %2 10) %1)]
   ["skip-next" "Last Page" #(identity %1)]])

(def change-page
  (u/debounce
    (fn [page]
      (f/dispatch [:form-item :fractals :sidebar :page page]))
    600 true))

(defn sidebar-pagination [_]
  (let [query-params (f/subscribe [:fractals-sidebar-query-params])]
    (fn [fractals _]
      (when fractals
        (p/letk [[page limit order] @query-params
                 [total-items] fractals
                 total-pages (u/ceil (/ total-items limit))
                 page-buffer (atom page)]
          [:div
           [:div.row.around-xs.middle-xs
            (for [[icon tooltip f] pagination-items]
              ^{:key icon}
              [ui/icon-button
               {:icon-class-name (str "mdi mdi-" icon)
                :icon-style      {:color (ui/color :grey700)}
                :tooltip         tooltip
                :on-touch-tap    #(change-page
                                   (reset! page-buffer (f total-pages @page-buffer)))}])]
           [:div.row.center-xs
            (str page "/" total-pages)]])))))
