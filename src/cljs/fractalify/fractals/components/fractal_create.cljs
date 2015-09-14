(ns fractalify.fractals.components.fractal-create
  (:require [re-frame.core :as f]
            [reagent.core :as r]
            [material-ui.core :as ui]
            [fractalify.fractals.components.canvas :as canvas]
            [fractalify.styles :as y]
            [fractalify.fractals.components.canvas-controls :as canvas-controls]
            [fractalify.utils :as u]
            [fractalify.components.form-input :as form-input]
            [fractalify.validators :as v]
            [fractalify.components.dialog-action :as dialog-action]
            [fractalify.fractals.components.fractal-page-layout :as fractal-page-layout]
            [fractalify.components.form :as form]))

(def title-maxlen 50)
(def desc-maxlen 140)

(defn centered-refresh-indicator [visible]
  [:div {:style y/canvas-refresh-indicator-wrap}
   [ui/refresh-indicator
    {:status "loading"
     :left   0
     :top    0
     :style  (merge
               y/canvas-refresh-indicator
               {:opacity (if visible 1 0)})}]])


(defn publish-confirm-btn []
  (dialog-action/dialog-action
    (let [form-errors (f/subscribe [:form-errors :fractals :info])]
      (fn []
        [ui/flat-button {:label      "Publish"
                         :ref        "publish"
                         :primary    true
                         :disabled   (not (empty? @form-errors))
                         :onTouchTap #(f/dispatch [:fractal-publish])}]))))

(def publish-form
  [form/form :fractals :info
   (fn [vals]
     (let [{:keys [title desc]} vals]
       [:div.row
        [:div.col-xs-12
         [form-input/text title "Title" [:fractals :info :title]
          {:required   true
           :validators [(v/length 0 title-maxlen)]}]]
        [:div.col-xs-12
         [form-input/text desc "Description" [:fractals :info :desc]
          {:multi-line true
           :validators [(v/length 0 desc-maxlen)]}]]]))])

(defn publish-dialog-props []
  {:title        "Publish Fractal"
   :action-focus "publish"
   :content      publish-form
   :actions      [{:text "Cancel"} (publish-confirm-btn)]})

(defn canvas-section []
  (let [l-system-generating (f/subscribe [:l-system-generating])]
    (fn []
      [:div
       [centered-refresh-indicator @l-system-generating]
       [canvas/canvas]])))

(defn btns-section []
  [:div.row.end-xs
   [ui/raised-button
    {:label        "Publish"
     :primary      true
     :on-touch-tap #(f/dispatch [:show-dialog (publish-dialog-props)])}]])

(defn sidebar-section []
  [canvas-controls/canvas-controls [:fractal-new]])

(defn fractal-create []
  [fractal-page-layout/fractal-page-layout
   [canvas-section]
   [btns-section]
   [sidebar-section]])

