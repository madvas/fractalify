(ns fractalify.components.snackbar
  (:require [reagent.core :as r]
            [material-ui.core :as ui]
            [re-frame.core :as f]
            [fractalify.utils :as u]))

(def ^:dynamic *snackbar-parent* (atom))

(defn- get-snackbar-ref []
  (.. @*snackbar-parent* -refs -snackbar))

(defn show-snackbar! []
  (if @*snackbar-parent*
    (.show (get-snackbar-ref))
    (u/mwarn "Snackbar: Attempt to show before mounted")))

(defn snackbar []
  (r/create-class
    {:component-did-mount (fn [this]
                            (reset! *snackbar-parent* this))
     :reagent-render
                          (let [props (f/subscribe [:snackbar-props])]
                            (fn []
                              [ui/snackbar (merge {:ref              "snackbar"
                                                   :autoHideDuration 10000
                                                   :message          ""}
                                                  @props)]))}))