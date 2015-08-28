(ns fractalify.fractals.lib.workers.turtle
  (:require-macros [servant.macros :refer [defservantfn]])
  (:require [fractalify.utils :as u]
            [schema.core :as s :include-macros true]
            [fractalify.fractals.schemas :as ch]
            [plumbing.core :as p]
            [servant.core :as servant]
            [servant.worker :as worker]))


(def cmd-map {"F" :forward
              "+" :left
              "-" :right
              "[" :push
              "]" :pop})

(defmulti command identity)

(s/defn move-coord :- s/Num
  [angle :- s/Num
   length :- s/Num
   type :- (s/enum :x :y)
   coord :- s/Num]
  (let [f (if (= type :x) Math/sin Math/cos)]
    (-> (f (* angle u/deg))
        (* length)
        (+ coord)
        (u/round 3))))

(s/defn turn
  [turtle :- ch/Turtle
   angle :- s/Num
   direction :- (s/=> s/Num s/Num)]
  (update turtle :angle #(direction % angle)))

(s/defn exec-cmd :- ch/Turtle
  [l-system :- ch/LSystem
   turtle :- ch/Turtle
   cmd :- s/Str]
  (command (cmd-map cmd) turtle l-system))

(s/defn gen-lines-coords :- ch/Lines
  [l-system :- ch/LSystem
   result-cmds :- s/Str]
  (p/letk [[origin start-angle] l-system]
    (let [turtle {:position origin
                  :angle    start-angle
                  :stack    '()
                  :lines    []}
          exec-fn (partial exec-cmd l-system)]
      (:lines (reduce exec-fn turtle result-cmds)))))


(s/defn update-turtle-lines :- ch/Turtle
  [turtle :- ch/Turtle
   old-pos :- (:position ch/Turtle)]
  (update turtle :lines
          #(conj % [old-pos (:position turtle)])))

(s/defn move-forward :- ch/Turtle
  [turtle :- ch/Turtle
   l-system :- ch/LSystem]
  (p/letk [[angle position] turtle
           [line-length] l-system]
    (let [move-fn (partial move-coord angle line-length)]
      (-> turtle
          (update-in [:position :x] (partial move-fn :x))
          (update-in [:position :y] (partial move-fn :y))
          (update-turtle-lines position)))))

(s/defn move-left :- ch/Turtle
  [turtle :- ch/Turtle
   l-system :- ch/LSystem]
  (turn turtle (:angle l-system) +))

(s/defn move-right :- ch/Turtle
  [turtle :- ch/Turtle
   l-system :- ch/LSystem]
  (turn turtle (:angle l-system) -))

(s/defn push-position :- ch/Turtle
  [turtle :- ch/Turtle _]
  (update turtle :stack #(cons (select-keys turtle [:position :angle]) %)))

(s/defn pop-position :- ch/Turtle
  [turtle :- ch/Turtle _]
  (-> turtle
      (merge turtle (first (:stack turtle)))
      (update :stack rest)))

(defmethod command :forward [_ & args] (apply move-forward args))
(defmethod command :left [_ & args] (apply move-left args))
(defmethod command :right [_ & args] (apply move-right args))
(defmethod command :push [_ & args] (apply push-position args))
(defmethod command :pop [_ & args] (apply pop-position args))
(defmethod command :default [_ & args] (apply identity args))

(defservantfn gen-lines-coords-worker [& args]
              (apply gen-lines-coords args))
