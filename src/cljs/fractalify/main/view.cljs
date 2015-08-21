(ns fractalify.main.view
  (:require [re-frame.core :as f]
            [reagent.core :as r]
            [fractalify.router :as t]
            [fractalify.main.components.header :as header]
            [fractalify.main.components.sidenav :as sidenav]
            [fractalify.components.snackbar :as snackbar]))

(def ^:dynamic *mui-theme*
  (.getCurrentTheme (js/MaterialUI.Styles.ThemeManager.)))

(defn main-view []
  (let [active-panel (f/subscribe [:active-panel])]
    (r/create-class
      {:display-name "Main Panel"

       :child-context-types
                     #js {:muiTheme js/React.PropTypes.object}

       :get-child-context
                     (fn [_]
                       #js {:muiTheme *mui-theme*})
       :reagent-render
                     (fn []
                       [:div
                        [header/header]
                        [sidenav/sidenav]
                        [snackbar/snackbar]
                        (t/panels @active-panel)])})))


