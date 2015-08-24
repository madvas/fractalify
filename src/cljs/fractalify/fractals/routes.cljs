(ns fractalify.fractals.routes
  (:require [re-frame.core :as r]
            [fractalify.router :as t]
            [fractalify.fractals.components.fractal-create :as fractal-edit]))

(defn define-routes! []

  (t/add-routes! {"fractals" {"/" {"create"     (t/perm :fractal-create [:login-required])
                                   [#"\d+" :id] :fractal-view
                                   }}
                  })

  (defmethod t/panels :fractal-create [] [fractal-edit/fractal-create])
  )
