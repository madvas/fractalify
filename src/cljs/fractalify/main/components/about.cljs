(ns fractalify.main.components.about)

(defn about []
  (fn []
    [:div "This is thee About Page."
     [:div [:a {:href "#/"} "go to Home Page"]]]))