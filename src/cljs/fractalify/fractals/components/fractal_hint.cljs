(ns fractalify.fractals.components.fractal-hint
  (:require [material-ui.core :as ui]
            [re-frame.core :as f]
            [fractalify.utils :as u]))

(defn hint-content []
  [:div "I dunno"])

(def dialog-props
  {:title        "What are L-Systems?"
   :action-focus "ok"
   :content      hint-content
   :actions      [{:text "OK" :ref "ok"}]})

(defn fractal-hint []
  [:div.col-xs-12.middle-xs.start-xs
   [:a.row.middle-xs.pad-lef-10.no-dec
    {:href     "#"
     :on-click #(f/dispatch [:show-dialog dialog-props])}
    [:h5.mar-rig-5 "What is this?"]
    [ui/font-icon
     {:class-name "mdi mdi-help-circle"
      :style      {:font-size "1.2em"}
      :color      (ui/color :grey600)}]]])