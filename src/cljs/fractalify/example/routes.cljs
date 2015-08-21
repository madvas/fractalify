(ns fractalify.example.routes
  (:require-macros [secretary.core :refer [defroute]])
  (:require [re-frame.core :as r]))

(defn app-routes []
  (defroute "/login" []
            (r/dispatch [:set-active-panel :login]))

  (defroute "/join" []
            (r/dispatch [:set-active-panel :join])))
