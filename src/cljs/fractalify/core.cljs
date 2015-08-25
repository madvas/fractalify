(ns fractalify.core
  (:require [reagent.core :as reagent]
            [re-frame.core :as re-frame]
            [fractalify.router :as router]
            [fractalify.main.handlers]
            [fractalify.main.subs]
            [fractalify.main.routes]
            [fractalify.main.view]
            [fractalify.users.handlers]
            [fractalify.users.subs]
            [fractalify.users.routes]
            [fractalify.fractals.handlers]
            [fractalify.fractals.subs]
            [fractalify.fractals.routes]
            [schema.core :as s]))

(enable-console-print!)


;(set! (.-DEBUG js/goog) false)
(s/set-fn-validation! goog.DEBUG)

(defn mount-root []
  (reagent/render [fractalify.main.view/main-view]
                  (.getElementById js/document "app")))

(fractalify.users.routes/define-routes!)
(fractalify.fractals.routes/define-routes!)
(fractalify.main.routes/define-routes!)
(router/start!)
(re-frame/dispatch-sync [:initialize-db])

(defn ^:export init []
  (mount-root))
