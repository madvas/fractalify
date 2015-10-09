(ns fractalify.fractals.routes
  (:require [fractalify.router :as t]
            [fractalify.fractals.components.fractal-create :as fractal-create]
            [fractalify.fractals.components.fractal-detail :as fractal-detail]))


(defn define-routes! []
  (t/add-routes! {"fractals"
                  {"/"
                   {"create" :fractal-create
                    [:id]    :fractal-detail}}})

  (defmethod t/panels :fractal-create [] [fractal-create/fractal-create])
  (defmethod t/panels :fractal-detail [] [fractal-detail/fractal-detail]))
