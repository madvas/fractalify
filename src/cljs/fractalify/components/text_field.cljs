(ns fractalify.components.text-field
  (:require-macros [fractalify.tracer-macros :refer [trace-views]]
                   [cljs.core.async.macros :refer [go go-loop]])
  (:require
    [reagent.core :as r]
    [fractalify.tracer]
    [material-ui.core :as ui]
    [re-frame.core :as f]
    [schema.core :as s :include-macros true]
    [fractalify.utils :as u]
    [fractalify.validators :as v]
    [fractalify.styles :as y]
    [plumbing.core :as p]
    [cljs.core.async :refer [chan close! >! <! sliding-buffer]]))


(def style (merge y/w-100 {:padding-bottom 13 :text-align "left"}))
(def underline-style {:bottom 22})
(def error-style {:text-align "left"})

(def params-schema [(s/cond-pre s/Keyword s/Int {:key s/Str} s/Str)])

(declare text-field)

(defn parse-val
  [evt type]
  (-> (u/e-val evt)
      (p/?> (= type "number")
            u/parse-float)))

(defn get-debounced-ch [change-ch props]
  (let [d (or (:debounce props) 0)]
    (apply u/debounce change-ch (if (number? d) [d] d))))

(s/defn text-field
  ([subscribe props]
    (text-field subscribe nil nil props))
  ([subscribe dispatch props]
    (text-field subscribe dispatch nil props))
  ([subscribe :- params-schema
    dispatch :- params-schema
    error-dispatch :- params-schema
    props :- {s/Keyword s/Any}]
    (let [value (f/subscribe subscribe)
          validators (into [] (concat
                                (when (:required props)
                                  [v/required])
                                (:validators props)))
          change-ch (chan)
          debounced-chan (get-debounced-ch change-ch props)]
      (when-let [default-value (:default-value props)]
        (f/dispatch (conj dispatch default-value)))
      (go
        (while true
          (let [val (<! debounced-chan)]
            (f/dispatch (conj dispatch val)))))
      (fn []
        (let [error-text (u/validate-until-error @value validators)]
          (when error-dispatch
            (f/dispatch (conj error-dispatch error-text)))
          [ui/text-field
           (merge
             {:default-value  @value
              :errorText      error-text
              :style          style
              :underlineStyle underline-style
              :errorStyle     error-style
              }
             (when dispatch
               {:on-change (fn [evt]
                             (let [val (parse-val evt (:type props))]
                               (go (>! change-ch val))))})
             props)])))))