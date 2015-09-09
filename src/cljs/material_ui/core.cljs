(ns material-ui.core
  (:require [reagent.core :as r]
            [camel-snake-kebab.core :as c]))


(do (def app-bar (reagent.core/adapt-react-class (clojure.core/aget js/MaterialUI "AppBar"))) (def app-canvas (reagent.core/adapt-react-class (clojure.core/aget js/MaterialUI "AppCanvas"))) (def avatar (reagent.core/adapt-react-class (clojure.core/aget js/MaterialUI "Avatar"))) (def flat-button (reagent.core/adapt-react-class (clojure.core/aget js/MaterialUI "FlatButton"))) (def raised-button (reagent.core/adapt-react-class (clojure.core/aget js/MaterialUI "RaisedButton"))) (def floating-action-button (reagent.core/adapt-react-class (clojure.core/aget js/MaterialUI "FloatingActionButton"))) (def card (reagent.core/adapt-react-class (clojure.core/aget js/MaterialUI "Card"))) (def card-header (reagent.core/adapt-react-class (clojure.core/aget js/MaterialUI "CardHeader"))) (def date-picker (reagent.core/adapt-react-class (clojure.core/aget js/MaterialUI "DatePicker"))) (def dialog (reagent.core/adapt-react-class (clojure.core/aget js/MaterialUI "Dialog"))) (def drop-down-menu (reagent.core/adapt-react-class (clojure.core/aget js/MaterialUI "DropDownMenu"))) (def drop-down-icon (reagent.core/adapt-react-class (clojure.core/aget js/MaterialUI "DropDownIcon"))) (def font-icon (reagent.core/adapt-react-class (clojure.core/aget js/MaterialUI "FontIcon"))) (def icon-button (reagent.core/adapt-react-class (clojure.core/aget js/MaterialUI "IconButton"))) (def icon-menu (reagent.core/adapt-react-class (clojure.core/aget js/MaterialUI "IconMenu"))) (def menu-item (reagent.core/adapt-react-class (clojure.core/aget js/MaterialUI "MenuItem"))) (def left-nav (reagent.core/adapt-react-class (clojure.core/aget js/MaterialUI "LeftNav"))) (def list (reagent.core/adapt-react-class (clojure.core/aget js/MaterialUI "List"))) (def list-item (reagent.core/adapt-react-class (clojure.core/aget js/MaterialUI "ListItem"))) (def list-divider (reagent.core/adapt-react-class (clojure.core/aget js/MaterialUI "ListDivider"))) (def menu (reagent.core/adapt-react-class (clojure.core/aget js/MaterialUI "Menu"))) (def menu-item (reagent.core/adapt-react-class (clojure.core/aget js/MaterialUI "MenuItem"))) (def paper (reagent.core/adapt-react-class (clojure.core/aget js/MaterialUI "Paper"))) (def linear-progress (reagent.core/adapt-react-class (clojure.core/aget js/MaterialUI "LinearProgress"))) (def circular-progress (reagent.core/adapt-react-class (clojure.core/aget js/MaterialUI "CircularProgress"))) (def refresh-indicator (reagent.core/adapt-react-class (clojure.core/aget js/MaterialUI "RefreshIndicator"))) (def slider (reagent.core/adapt-react-class (clojure.core/aget js/MaterialUI "Slider"))) (def checkbox (reagent.core/adapt-react-class (clojure.core/aget js/MaterialUI "Checkbox"))) (def snackbar (reagent.core/adapt-react-class (clojure.core/aget js/MaterialUI "Snackbar"))) (def table (reagent.core/adapt-react-class (clojure.core/aget js/MaterialUI "Table"))) (def tabs (reagent.core/adapt-react-class (clojure.core/aget js/MaterialUI "Tabs"))) (def tab (reagent.core/adapt-react-class (clojure.core/aget js/MaterialUI "Tab"))) (def text-field (reagent.core/adapt-react-class (clojure.core/aget js/MaterialUI "TextField"))) (def select-field (reagent.core/adapt-react-class (clojure.core/aget js/MaterialUI "SelectField"))) (def time-picker (reagent.core/adapt-react-class (clojure.core/aget js/MaterialUI "TimePicker"))) (def toolbar (reagent.core/adapt-react-class (clojure.core/aget js/MaterialUI "Toolbar"))) (def toolbar-group (reagent.core/adapt-react-class (clojure.core/aget js/MaterialUI "ToolbarGroup"))) (def toolbar-separator (reagent.core/adapt-react-class (clojure.core/aget js/MaterialUI "ToolbarSeparator"))))

(def ThemeManager (new js/MaterialUI.Styles.ThemeManager))

(def menu-item-link-type (aget js/MaterialUI "MenuItem" "Types" "LINK"))

(defn get-current-theme [] (js->clj (.getCurrentTheme ThemeManager)))
(defn set-theme! [theme]
  (.setTheme ThemeManager (clj->js theme)))

(defn set-palette! [pal]
  (.setPalette ThemeManager (clj->js pal)))

(defn get-palette []
  (js->clj (aget ThemeManager "palette")))

(defn set-component-themes! [overrides]
  (.setComponentThemes ThemeManager (clj->js overrides)))

(defn palette-color [color-type]
  ((get-palette) (c/->camelCase (name color-type))))

(defn set-spacing! [spacing] (.setSpacing ThemeManager (clj->js spacing)))

(def DARK-THEME (js->clj (aget ThemeManager "types" "DARK")))
(def LIGHT-THEME (js->clj (aget ThemeManager "types" "LIGHT")))

(defn color [color-key]
  (aget js/MaterialUI "Styles" "Colors" (c/->camelCase (name color-key))))

(defn mui-theme-wrap [form]
  (r/create-class
    {:child-context-types
     #js {:muiTheme (aget js/React "PropTypes" "object")}
     :get-child-context
     (fn [_]
       (clj->js {:muiTheme (get-current-theme)}))
     :reagent-render
     (fn []
       form)}))