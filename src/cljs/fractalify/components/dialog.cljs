(ns fractalify.components.dialog
  (:require [reagent.core :as r]
            [material-ui.core :as ui]
            [re-frame.core :as f]))

(def ^:dynamic *dialog-parent* (atom))
(def ^:dynamic *queued-dialog* (atom))

(defn- get-dialog-ref []
  (aget @*dialog-parent* "refs" "dialog"))

(defn show-dialog! []
  (if @*dialog-parent*
    (.show (get-dialog-ref))
    (reset! *queued-dialog* true)))

(defn hide-dialog! []
  (.dismiss (get-dialog-ref)))

(add-watch *dialog-parent* :dialog
           (fn [_ _ old-state _]
             (when (and (nil? old-state) @*queued-dialog*)
               (show-dialog!)
               (remove-watch *dialog-parent* :dialog))))

(defn dialog []
  (r/create-class
    {:component-did-mount (fn [this]
                            (reset! *dialog-parent* this))
     :reagent-render
                          (let [props (f/subscribe [:dialog-props])]
                            (fn []
                              [ui/dialog (merge {:ref "dialog"}
                                                @props)
                               (:content @props)]))}))