(ns fractalify.workers.turtle
  (:require [schema.core :as s :include-macros true]
            [workers.core :as w]
            [fractalify.workers.schemas :as ch]
            [plumbing.core :as p]
            [schema.coerce :as coerce]))

(enable-console-print!)
(s/set-fn-validation! false)

(s/defn round
  [d :- s/Num
   precision :- s/Int]
  (let [factor (Math/pow 10 precision)]
    (/ (Math/round (* d factor)) factor)))

(def deg (/ (.-PI js/Math) 180))

(defmulti command identity)

(s/defn move-coord :- s/Num
  [angle :- s/Num
   length :- s/Num
   type :- (s/enum :x :y)
   coord :- s/Num]
  (let [f (if (= type :x) Math/sin Math/cos)]
    (-> (f (* angle deg))
        (* length)
        (+ coord)
        (round 3))))

(s/defn turn
  [turtle :- ch/Turtle
   angle :- s/Num
   direction :- (s/=> s/Num s/Num)]
  (update turtle :angle #(direction % angle)))

(s/defn exec-cmd :- ch/Turtle
  [l-system :- ch/LSystem
   cmd-map :- {s/Str s/Keyword}
   turtle :- ch/Turtle
   cmd :- s/Str]
  (command (cmd-map cmd) turtle l-system))

(s/defn gen-lines-coords :- ch/Lines
  [l-system :- ch/LSystem
   result-cmds :- s/Str]
  (p/letk [[origin start-angle] l-system
           turtle {:position origin
                   :angle    start-angle
                   :stack    '()
                   :lines    []}
           cmd-map (into {} (vals (:cmds l-system)))
           exec-fn (partial exec-cmd l-system cmd-map)]
          (:lines (reduce exec-fn turtle result-cmds))))


(s/defn update-turtle-lines :- ch/Turtle
  [turtle :- ch/Turtle
   old-pos :- (:position ch/Turtle)]
  (update turtle :lines
          #(conj % [old-pos (:position turtle)])))

(s/defmethod
  command :forward :- ch/Turtle
  [_ turtle :- ch/Turtle
   l-system :- ch/LSystem]
  (p/letk [[angle position] turtle
           [line-length] l-system
           move-fn (partial move-coord angle line-length)]
          (-> turtle
              (update-in [:position :x] (partial move-fn :x))
              (update-in [:position :y] (partial move-fn :y))
              (update-turtle-lines position))))

(s/defmethod
  command :left :- ch/Turtle
  [_ turtle :- ch/Turtle
   l-system :- ch/LSystem]
  (turn turtle (:angle l-system) +))

(s/defmethod
  command :right :- ch/Turtle
  [_ turtle :- ch/Turtle
   l-system :- ch/LSystem]
  (turn turtle (:angle l-system) -))

(s/defmethod
  command :push :- ch/Turtle
  [_ turtle :- ch/Turtle
   l-system :- ch/LSystem]
  (update turtle :stack #(cons (select-keys turtle [:position :angle]) %)))

(s/defmethod
  command :pop :- ch/Turtle
  [_ turtle :- ch/Turtle
   l-system :- ch/LSystem]
  (-> turtle
      (merge turtle (first (:stack turtle)))
      (update :stack rest)))

(s/defmethod
  command :default :- ch/Turtle
  [_ turtle :- ch/Turtle
   l-system :- ch/LSystem]
  turtle)

(def l-system-coercer (coerce/coercer ch/LSystem ch/l-system-coercion-matcher))

(w/on-message (fn [[l-system-data result-cmds]]
                (let [l-system (l-system-coercer l-system-data)]
                  (w/post-message (gen-lines-coords l-system result-cmds)))))
