(ns fractalify.fractals.subs
  (:require-macros [reagent.ratom :refer [reaction]]
                   [fractalify.tracer-macros :refer [trace-subs]])
  (:require [re-frame.core :as r]
            [reagent.ratom :refer [make-reaction]]
            [fractalify.tracer :refer [tracer]]))

#_(trace-subs

  #_(r/register-sub
   :new-fractal
   (fn [db _]
     (reaction (:new-fractal @db)))))
