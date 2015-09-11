(ns fractalify.main.view
  (:require [re-frame.core :as f]
            [reagent.core :as r]
            [fractalify.router :as t]
            [fractalify.main.components.header :as header]
            [fractalify.main.components.sidenav :as sidenav]
            [fractalify.components.snackbar :as snackbar]
            [fractalify.main.components.footer :as footer]
            [fractalify.components.dialog :as dialog]
            [material-ui.core :as ui]
            [fractalify.utils :as u]))



(defn a [props]
  (r/create-class
    {:should-component-update
     (fn [this old-argv new-argv]
       (println "should-component-update" old-argv new-argv))
     :component-will-receive-props
     (fn [this new-argv]
       (println ":component-will-receive-props " " - " (r/props this))
       (println new-argv))
     :reagent-render
     (fn [props]
       [:div "hello " (:something props)])}))

(defn main-layout []
  (let [active-panel (f/subscribe [:active-panel])
        sth (atom "here")]
    (u/set-timeout (fn []
                     (reset! sth "birds")
                     (r/flush))
                   1000)
    (fn []
      #_ [a {:something @sth}]
      [ui/app-canvas
       [header/header]
       [sidenav/sidenav]
       [snackbar/snackbar]
       [dialog/dialog]
       [:div.main-body
        (t/panels @active-panel)]
       [footer/footer]])))

(defn main-view []
  (ui/set-palette!
    {:canvasColor "#F2F2F2"})

  [ui/mui-theme-wrap
   [main-layout]])


