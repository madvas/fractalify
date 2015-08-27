(ns fractalify.fractals.routes
  (:require [fractalify.router :as t]
            [fractalify.fractals.components.fractal-create :as fractal-create]))


(defn define-routes! []
  (t/add-routes! {"fractals"
                  {"/"
                   {"create"     (t/perm :fractal-create [:login-required])
                    [#"\d+" :id] :fractal-view}}})

  (defmethod t/panels :fractal-create [] [fractal-create/fractal-create]))
