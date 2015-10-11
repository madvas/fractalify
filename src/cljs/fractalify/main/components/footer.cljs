(ns fractalify.main.components.footer
  (:require
    [re-frame.core :as f]
    [reagent.core :as r]
    [material-ui.core :as ui]
    [fractalify.router :as t]
    [fractalify.styles :as y]))

(def social-btn-style
  {:position :absolute
   :bottom   10
   :right    30})

(defn footer []
  [:footer.row.middle-xs.center-xs.relative
   [:a {:href (t/url :about)} "About"]
   [:a.mar-lef-10 {:href "http://disapainted.com" :target "_blank"} "disapainted.com"]
   [:div {:style social-btn-style}
    [:a {:class "twitter-share-button"
         :href  "https://twitter.com/intent/tweet?hashtag=fractalify&url=http://fractalify.com"} "Tweet"]]
   [:div {:style (merge social-btn-style
                        {:right (+ (:right social-btn-style) 60)
                         :width 80})}
    [:iframe {:src          "https://ghbtns.com/github-btn.html?user=madvas&repo=fractalify&type=star&count=true"
              :frame-border 0
              :scrolling    0
              :width        "80px"
              :height       "20px"}]]])

