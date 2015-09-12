(ns fractalify.components.form-select
  (:require
    [material-ui.core :as ui]
    [re-frame.core :as f]
    [schema.core :as s :include-macros true]
    [fractalify.utils :as u]
    [plumbing.core :as p]
    [instar.core :as i]
    [clojure.walk :as w]
    [fractalify.main.schemas :as ch]))

(def default-val-member :payload)
(def default-display-member :text)

(def style {:width "100%"})
(def MenuItems [{(s/cond-pre s/Str s/Keyword s/Num) s/Any}])

(s/defn parse-val [evt menu-items :- MenuItems val-member]
  "Hack to retrieve value types other than string, because e.target.value
  always returns string (no keywords)"
  (let [val (u/e-val evt)]
    (-> menu-items
        (i/get-values-in-paths [* val-member])
        (#(zipmap % (u/range-count %)))
        w/stringify-keys
        (#(nth menu-items (% val)))
        (get val-member))))

(s/defn get-member-names [props]
  (p/letk [[{value-member default-val-member}] props
           [{display-member default-display-member}] props]
    [value-member display-member]))

(s/defn form-select
  ([path :- ch/DbPath
    props :- {s/Keyword s/Any}]
    (let [value (f/subscribe (into [:form-item] path))
          val-member (first (get-member-names props))]
      (fn []
        [ui/select-field
         (merge {:value-member   default-val-member
                 :display-member default-display-member
                 :value          @value
                 :style          style
                 :on-change      #(f/dispatch
                                   (u/concat-vec
                                     [:form-item]
                                     path
                                     [(parse-val % (:menu-items props) val-member)]))}
                props)]))))