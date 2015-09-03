(ns fractalify.fractals.subs
  (:require-macros [reagent.ratom :refer [reaction]]
                   [fractalify.tracer-macros :refer [trace-subs]])
  (:require [re-frame.core :as r]
            [reagent.ratom :refer [make-reaction]]
            [fractalify.tracer :refer [tracer]]))

(trace-subs
  (r/register-sub
    :new-l-system
    (fn [db _]
      (reaction (get-in @db [:forms :l-system]))))

  (r/register-sub
    :l-system-generating
    (fn [db _]
      (reaction (get-in @db [:l-system-generating])))))
