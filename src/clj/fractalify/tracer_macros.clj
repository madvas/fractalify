(ns fractalify.tracer-macros
  (:require [clairvoyant.core :as c]
            [clojure.walk :as w]))

(defn- make-symbol [fn-name suffix]
  (symbol (str (name fn-name) suffix)))

(defn- insert-nth [coll index val]
  (concat (take index coll) [val] (take-last (- (count coll) index) coll)))

(defn- replace-in-list [coll n x]
  (concat (take n coll) (list x) (nthnext coll (inc n))))

(defn- replace-last-in-list [coll x]
  (replace-in-list coll (dec (count coll)) x))

(defn- replace-in-sublist [coll ns x]
  (if (seq ns)
    (let [sublist (nth coll (first ns))]
      (replace-in-list coll
                       (first ns)
                       (replace-in-sublist sublist (rest ns) x)))
    x))

(defn- name-anonymous-fns [body fn-name]
  (let [c (atom 0)
        fn-name (name fn-name)]
    (w/postwalk (fn [p]
                  (if (and (symbol? p) (= (name p) "fn"))
                    (do (reset! c (inc @c))
                        (concat '(fn) (list (symbol (str fn-name (if (> @c 1) @c ""))))))
                    p))
                body)))

(defn- wrap-into-fn [fn-name body]
  (concat '(fn) (list fn-name) '([]) (list body)))

(defn- insert-wrap-fn-name [sub fn-name]
  (replace-in-list sub 2
                   (insert-nth (nth sub 2) 1 (make-symbol fn-name "-sub"))))

(defn- transform-to-make-reaction [sub fn-name]
  (let [reaction-macro (-> sub
                           (nth 2)
                           (nth 3))
        fn-body (second reaction-macro)]
    (if-not (= (name (first reaction-macro)) "reaction")
      (println "Missing usage of \"reaction\" macro " reaction-macro)
      (let [make-reaction-fn
            (replace-in-list reaction-macro 0
                             'reagent.ratom/make-reaction)]
        (-> sub
            (replace-in-sublist [2 3] make-reaction-fn)
            (replace-in-sublist
              [2 3 1]
              (wrap-into-fn (make-symbol fn-name "-rct") fn-body)))))))

(defn- rewrite-sub [sub]
  (let [keyword-name (second sub)]
    (-> sub
        (insert-wrap-fn-name keyword-name)
        (transform-to-make-reaction keyword-name))
    ))

(defn- rewrite-handler [handler]
  (let [keyword-name (second handler)
        fn-body (last handler)]
    (replace-last-in-list handler
                          (insert-nth fn-body 1 (make-symbol keyword-name "-hnd")))))

(defn- rewrite-view [view]
  (let [fn-name (make-symbol (second view) "")]
    (name-anonymous-fns view fn-name)))

(declare trace-subs)
(declare trace-handlers)
(declare trace-views)

#_(macroexpand-1
  '(trace-views
     (defn canvas []
       (let [params (f/subscribe [:route-params])]
         (r/create-class
           {
            :component-will-update (fn [next-props next-state]
                                     (println ":component-will-update"))
            :reagent-render
                                   (fn []
                                     [:canvas {:style y/canvas}])})))))

#_(macroexpand-1
    '(trace-subs
       (r/register-sub
         :get-form-item
         (fn [db [_ form item index]]
           (reaction (let [form-item (get-in @db [:forms form item])]
                       (if (and (vector? form-item) index)
                         (nth form-item index)
                         form-item)))))

       ))
#_(macroexpand-1
    '(trace-handlers

       (r/register-handler
         :dissoc-db
         m/standard-middlewares
         (fn [db [key]]
           (dissoc db key)))

       (r/register-handler
         :initialize-db
         m/standard-without-debug
         (fn [_]
           db/default-db))))


(defn trace-forms-args [color]
  `{:tracer (fractalify.tracer/tracer :color ~color)})

(defmacro trace-subs [& subs]
  (let [new-subs (map rewrite-sub subs)]
    `(clairvoyant.core/trace-forms ~(trace-forms-args "brown") ~@new-subs)))

(defmacro trace-handlers [& handlers]
  (let [new-handlers (map rewrite-handler handlers)]
    `(clairvoyant.core/trace-forms ~(trace-forms-args "green") ~@new-handlers)))

(defmacro trace-views [& views]
  (let [new-views (map rewrite-view views)]
    `(clairvoyant.core/trace-forms ~(trace-forms-args "violet") ~@new-views)))