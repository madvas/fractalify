(ns fractalify.components.responsive-panel)

(defn responsive-panel [children]
  [:div.row.middle-xs.center-xs
   [:div.col-xs-12.col-sm-8.col-md-6.col-lg-4
    children]])

