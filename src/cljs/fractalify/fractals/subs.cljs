(ns fractalify.fractals.subs
  (:require-macros [reagent.ratom :refer [reaction]]
                   [fractalify.tracer-macros :refer [trace-subs]])
  (:require [re-frame.core :as r]
            [fractalify.tracer]))

(trace-subs
  (r/register-sub
    :l-system-new
    (fn [db _]
      (reaction (get-in @db [:fractals :forms :l-system]))))

  (r/register-sub
    :l-system-generating
    (fn [db _]
      (reaction (get-in @db [:fractals :l-system-generating]))))

  (r/register-sub
    :canvas
    (fn [db _]
      (reaction (get-in @db [:fractals :forms :canvas]))))

  (r/register-sub
    :all-cmds
    (fn [db _]
      (reaction (get-in @db [:fractals :all-cmds]))))

  (r/register-sub
    :fractal-detail
    (fn [db _]
      (reaction (get-in @db [:fractals :fractal-detail])))))
