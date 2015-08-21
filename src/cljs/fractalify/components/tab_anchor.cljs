(ns fractalify.components.tab-anchor
  (:require
    [reagent.core :as r]
    [material-ui.core :as ui]
    [fractalify.styles :as y]))

(defn tab-anchor [props children]
  ^{:key (:href props)}
  [ui/tab
   {:label (r/as-element [:a {:href  (:href props)
                              :style y/tab-anchor
                              :class "no-dec"}
                          (:label props)])}
   children])