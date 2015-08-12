(ns fractalify.views
  (:require [re-frame.core :as rf]
            [reagent.core :as r]
            [material-ui.core :as ui :include-macros true]))

;; --------------------
(defn home-panel []
  (let [name (rf/subscribe [:name])]
    (fn []
      [:div (str "Hello from " @name ". This is the Home Page.")
       [:div [:a {:href "#/about"} "go to About Page"]]
       [ui/TextField {:hintText          "Please enter your first name"
                      :floatingLabelText "First Name"}]
       [ui/FlatButton {:label "Mater"}]
       [ui/Slider {:name "slide1" :on-change #((println "here"))}]])))


(defn about-panel []
  (fn []
    [:div "This is the About Page."
     [:div [:a {:href "#/"} "go to Home Page"]]]))

;; --------------------
(defmulti panels identity)
(defmethod panels :home-panel [] [home-panel])
(defmethod panels :about-panel [] [about-panel])
(defmethod panels :default [] [:div])

(defn main-panel []
  (let [active-panel (rf/subscribe [:active-panel])]
    (r/create-class
      {:display-name "Main Panel"

       :child-context-types
                     #js {:muiTheme js/React.PropTypes.object}

       :get-child-context
                     (fn [this]
                       #js {:muiTheme (.getCurrentTheme js/ThemeManager)})
       :reagent-render
                     (fn []
                       (panels @active-panel))})))
