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
    (->> body
         (w/prewalk
           (fn [x]
             (if (and (list? x)
                      (symbol? (first x))
                      (= (name (first x)) "fn")
                      (vector? (second x)))
               (do
                 (reset! c (inc @c))
                 (insert-nth
                   x 1
                   (symbol (str fn-name (if (> @c 1) @c "")))))
               x))))))

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
        (transform-to-make-reaction keyword-name))))

(defn- rewrite-handler [handler]
  (let [keyword-name (second handler)]
    (name-anonymous-fns handler (make-symbol keyword-name "-hnd"))))

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
#_ (macroexpand-1
  '(trace-handlers
     (r/register-handler
       :assoc-db
       m/standard-middlewares
       (fn [db [key value]]
         (assoc db key value)))

     (r/register-handler
       :dissoc-db
       m/standard-middlewares
       (fn [db [key]]
         (dissoc db key)))

     (r/register-handler
       :initialize-db
       m/standard-without-debug
       (fn [_]
         db/default-db))

     (r/register-handler
       :set-active-panel
       m/standard-middlewares
       (fn [db [active-panel permissions]]
         (if-let [error (p/validate-permissions db permissions)]
           (do (r/dispatch [:show-snackbar (select-keys error [:message])])
               (t/go! (:redirect error))
               db)
           (assoc db :active-panel active-panel))))

     (r/register-handler
       :set-form-item
       m/standard-middlewares
       (fn [db params]
         (let [value (last params)
               path (into [] (butlast params))]
           (assoc-in db (into [:forms] path) value))))

     (r/register-handler
       :set-form-error
       m/standard-without-debug
       (fn [db [form-name & params]]
         (let [value (last params)
               item-path (into [] (butlast params))
               path (into [:forms form-name :errors] item-path)]
           (if value
             (assoc-in db path value)
             (u/dissoc-in db path)))))

     (r/register-handler
       :show-snackbar
       m/standard-without-debug
       (fn [db [snackbar-props]]
         (let [db (assoc db :snackbar-props snackbar-props)]
           (snackbar/show-snackbar!)
           db)))

     (r/register-handler
       :sidenav-action
       m/standard-without-debug
       (fn [db [action]]
         (cond
           (= action :toggle) (sidenav/toggle-sidenav!)
           (= action :close) (sidenav/close-sidenav!))
         db))))

#_(macroexpand-1
    '(trace-views
       (s/defn text-field
               ([subscribe props]
                 (text-field subscribe nil nil props))
               ([subscribe dispatch props]
                 (text-field subscribe dispatch nil props))
               ([subscribe :- [(s/either s/Keyword s/Int)]
                 dispatch :- [(s/either s/Keyword s/Int)]
                 error-dispatch :- [(s/either s/Keyword s/Int)]
                 props :- {s/Keyword s/Any}]
                 (let [value (f/subscribe subscribe)
                       validators (into [] (concat
                                             (when (:required props)
                                               [v/required])
                                             (:validators props)))]
                   (when-let [default-value (:default-value props)]
                     (f/dispatch (conj dispatch default-value)))
                   (fn []
                     (let [error-text (u/validate-until-error @value validators)]
                       (when error-dispatch
                         (f/dispatch (conj error-dispatch error-text)))
                       [ui/text-field (merge
                                        {:value          @value
                                         :errorText      error-text
                                         :style          style
                                         :underlineStyle underline-style
                                         :errorStyle     error-style
                                         }
                                        (when dispatch
                                          {:onChange #(f/dispatch (conj dispatch (u/e-val %)))})
                                        props)])))))))


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