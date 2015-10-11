(ns fractalify.fractals.subs
  (:require-macros [reagent.ratom :refer [reaction]]
                   [fractalify.tracer-macros :refer [trace-subs]])
  (:require [fractalify.tracer]
            [re-frame.core :as f]
            [fractalify.utils :as u]
            [fractalify.handler-utils :as d]
            [fractalify.router :as t]
            [schema.core :as s :include-macros true]
            [fractalify.fractals.schemas :as fch]))


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
  :fractal-new
  (fn [db _]
    (reaction (-> @db
                  (get-in [:fractals :forms])
                  (select-keys [:canvas :l-system])))))

(f/register-sub
  :fractal-detail
  (fn [db _]
    (reaction (get-in @db [:fractals :fractal-detail]))))

(f/register-sub
  :fractal-comments
  (fn [db _]
    (reaction (get-in @db [:fractals :fractal-detail :comments]))))

(f/register-sub
  :fractals-home
  (s/fn [db [sub type :- fch/FractalOrderTypes]]
    (reaction (get-in @db [:fractals :fractals-home type]))))

(f/register-sub
  :fractals-home-query-params
  (s/fn [db [sub type :- fch/FractalOrderTypes]]
    (reaction {:sort type :sort-dir -1 :limit 10})))

(f/register-sub
  :fractals-sidebar
  (fn [db _]
    (reaction (get-in @db [:fractals :fractals-sidebar]))))

(f/register-sub
  :fractals-sidebar-query-params
  (fn [db _]
    (reaction (get-in @db [:fractals :forms :sidebar]))))

(f/register-sub
  :fractals-user
  (fn [db _]
    (reaction (get-in @db [:fractals :fractals-user]))))

(f/register-sub
  :fractals-user-query-params
  (s/fn [db]
    (let [route-params (f/subscribe [:route-params])]
      (reaction (merge {:limit    1000
                        :sort     :created
                        :sort-dir -1
                        :username (:username route-params)}
                       (u/select-key @db :username))))))


