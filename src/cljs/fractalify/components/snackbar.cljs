(ns fractalify.components.snackbar
  (:require [reagent.core :as r]
            [material-ui.core :as ui]
            [re-frame.core :as f]
            [fractalify.utils :as u]))

(def ^:dynamic *snackbar-parent* (atom))
(def ^:dynamic *queued-snackbar* (atom))

(defn- get-snackbar-ref []
  (aget @*snackbar-parent* "refs" "snackbar"))

(defn show-snackbar! []
  (if @*snackbar-parent*
    (.show (get-snackbar-ref))
    (reset! *queued-snackbar* true)))

(add-watch *snackbar-parent* :snackbar
           (fn [_ _ old-state _]
             (when (and (nil? old-state) @*queued-snackbar*)
               (show-snackbar!)
               (remove-watch *snackbar-parent* :snackbar))))

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