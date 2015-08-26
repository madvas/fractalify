(ns l-system.core)

(def ^:dynamic *grammar* nil)

(def ^:dynamic *env* {:origin [200 200]
                      :n-productions 4
                      :line-length 7
                      :start-angle 180})

(defn apply-rules [grammar pattern]
  (apply str
         (replace (:rules grammar) pattern)))

(defn gen-commands [grammar n]
  (nth
    (iterate
      (partial apply-rules grammar) (:start grammar))
    n))

(defn- new-position [turtle length]
  (let [deg   (/ (Math/PI) 180)
        angle (:angle turtle)]
    {:angle angle
     :x (-> (Math/sin (* angle deg))
            (* length)
            (+ (:x turtle)))
     :y (-> (Math/cos (* angle deg))
            (* length)
            (+ (:y turtle)))}))

(defn- turn [turtle direction angle]
  (assoc turtle
    :angle
    (direction (:angle turtle) angle)))

(defn- exec-cmd [turtle command]
  "Returns a new turtle map consisting of a current turtle position, a
  line drawing coordinates & a FIFO stack of 'paused' turtle positions"
  (let [angle (:angle *grammar*)
        length (:line-length *env*)
        cmd ((:cmd-map *grammar*) command)
        {current-pos :current-pos stack :stack} turtle]
    (condp = cmd
      :left    (assoc turtle :current-pos (turn current-pos + angle))
      :push    (assoc turtle :stack (cons current-pos stack))
      :pop     (assoc turtle :current-pos (first stack) :stack (rest stack))
      :right   (assoc turtle :current-pos (turn current-pos - angle))
      :forward (let [new-turtle (new-position current-pos length)
                     {x1 :x y1 :y} current-pos
                     {x2 :x y2 :y} new-turtle
                     new-lines (conj (:lines turtle) [x1 y1 x2 y2])]
                 (assoc turtle :current-pos new-turtle :lines new-lines))
      turtle)))

(defn gen-coords [grammar env]
  "Generates line drawing coordinates for an l-system grammar & start environment"
  (binding [*grammar* grammar
            *env*  env]
    (let [origin {:x (first (:origin env))
                  :y (second (:origin env))
                  :angle (:start-angle env)}
          turtle {:current-pos origin :stack '() :lines []}
          commands (gen-commands grammar (:n-productions env))]
      (:lines
        (reduce exec-cmd turtle commands)))))
