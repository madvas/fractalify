(ns fractalify.fractals.subs
  (:require-macros [reagent.ratom :refer [reaction]]
                   [fractalify.tracer-macros :refer [trace-subs]])
  (:require [fractalify.tracer]
            [re-frame.core :as f]
            [fractalify.utils :as u]
            [fractalify.db-utils :as d]
            [fractalify.router :as t]))

(trace-subs
  (f/register-sub
    :l-system-new
    (fn [db _]
      (reaction (get-in @db [:fractals :forms :l-system]))))

  (f/register-sub
    :l-system-generating
    (fn [db _]
      (reaction (get-in @db [:fractals :l-system-generating]))))

  (f/register-sub
    :canvas
    (fn [db _]
      (reaction (get-in @db [:fractals :forms :canvas]))))

  (f/register-sub
    :all-cmds
    (fn [db _]
      (reaction (get-in @db [:fractals :all-cmds]))))

  (f/register-sub
    :fractal-detail-query
    (fn [db _]
      (reaction (d/query @db [:fractals :fractal-detail] (:route-params @db) :fractal-detail))))

  (f/register-sub
    :fractal-comments-query
    (fn [db _]
      (reaction (d/query @db [:fractals :fractal-detail :comments]
                         (:route-params @db)
                         :fractal-detail))))
  )
