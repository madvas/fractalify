(ns fractalify.main.view
  (:require [re-frame.core :as f]
            [fractalify.router :as t]
            [fractalify.main.components.header :as header]
            [fractalify.main.components.sidenav :as sidenav]
            [fractalify.components.snackbar :as snackbar]
            [fractalify.main.components.footer :as footer]
            [fractalify.components.dialog :as dialog]
            [material-ui.core :as ui]
            [fractalify.styles :as y]))

(defn main-layout []
  (let [active-panel (f/subscribe [:active-panel])]
    (fn []
      [ui/app-canvas
       [header/header]
       [sidenav/sidenav]
       [snackbar/snackbar]
       [dialog/dialog]
       [:div {:style y/main-body}
        (t/panels @active-panel)]
       [footer/footer]])))

(defn main-view []
  (ui/set-palette!
    {:canvasColor "#F2F2F2"})

  [ui/mui-theme-wrap
   [main-layout]])


