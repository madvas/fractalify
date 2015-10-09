(ns fractalify.handler-utils
  (:require [fractalify.utils :as u]
            [plumbing.core :as p]
            [cljs.core]
            [schema.core :as s :include-macros true]
            [fractalify.main.schemas :as ch]
            [fractalify.router :as t]
            [re-frame.core :as f]))

(defn logged-user [db]
  (get-in db [:users :logged-user]))

(defn get-form-data [db module form]
  (-> db
      (get-in [module :forms form])
      (dissoc :errors)))

(s/defn path-query-params :- (s/maybe ch/QueryParams)
  [db path :- ch/DbPath]
  (:query-params (get-in db [:queries path])))

(s/defn assoc-path-query-params
  [db
   path :- ch/DbPath
   query-params :- ch/QueryParams]
  (assoc-in db [:queries path] {:query-params query-params}))

(s/defn assoc-query-loading [db path :- ch/DbPath v :- s/Bool]
  (assoc-in db [:queries path :loading?] v))

(s/defn query-loading? :- (s/maybe s/Bool)
  [db path :- ch/DbPath]
  (get-in db [:queries path :loading?]))

(s/defn assoc-with-query-params
  ([db
    path :- ch/DbPath
    val
    query-params :- ch/QueryParams]
    (-> db
        (assoc-in path val)
        (assoc-path-query-params path query-params))))

(defn show-snackbar [snackbar-text]
  (f/dispatch [:show-snackbar snackbar-text]))

(defn snack-n-go! [snackbar-text route]                     ; :)
  (show-snackbar snackbar-text)
  (t/go! route))

(s/defn create-dispatch [key :- s/Keyword]
  #(f/dispatch (into [key] %&)))

(s/defn create-handler
  ([x] (create-handler x identity))
  ([x default-handler] (cond (keyword? x) (create-dispatch x)
                             (u/function? x) x
                             :else default-handler)))

(defn default-send-err-handler [undo? err]
  (when undo?
    (f/dispatch [:undo]))
  (f/dispatch [:show-snackbar "Oops, something went awfully wrong :("]))

(defn create-send-error-handler
  ([undo? x]
   (if (map? x)
     (create-handler
       (p/fnk [status]
         (if-let [snack-text (x status)]
           (show-snackbar snack-text)
           (default-send-err-handler undo? ""))))
     (create-handler x (partial default-send-err-handler undo?)))))
