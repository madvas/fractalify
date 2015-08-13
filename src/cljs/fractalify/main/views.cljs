(ns fractalify.main.views
  (:require [re-frame.core :as rf]
            [reagent.core :as r]
            #_ [material-ui.core :as ui]))

;; --------------------
(defn home-panel []
  (let [name (rf/subscribe [:name])]
    (fn []
      [:div (str "Hello from " @name ". This is the Home Page.")
       [:div [:a {:href "#/about"} "go to About Page"]]
       ;[ui/text-field {:hintText          "Please enter your first name"
       ;               :floatingLabelText "First Name"}]
       ;[ui/flat-button {:label "Mater"}]
       ;[ui/avatar {:src "http://material-ui.com/images/uxceo-128.jpg"}]


       ])))


(defn about-panel []
  (fn []
    [:div "This is the About Page."
     [:div [:a {:href "#/"} "go to Home Page"]]]))


(defn header []
  (fn []

    [:div "Header"]))

;; --------------------
(defmulti panels identity)
(defmethod panels :home-panel [] [home-panel])
(defmethod panels :about-panel [] [about-panel])
(defmethod panels :default [] [:div])

;(def ^:dynamic *mui-theme*
;  (.getCurrentTheme (js/MaterialUI.Styles.ThemeManager.)))

(defn main-panel []
  (let [active-panel (rf/subscribe [:active-panel])]
    (fn []

      (panels @active-panel))))


;(r/create-class
;  {:display-name "Main Panel"
;
;   :child-context-types
;                 #js {:muiTheme js/React.PropTypes.object}
;
;   :get-child-context
;                 (fn [_]
;                   #js {:muiTheme *mui-theme*})
;   :reagent-render
;                 (fn []
;                   [:div
;                    [header]
;                    (panels @active-panel)])})