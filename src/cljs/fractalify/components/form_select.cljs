(ns fractalify.components.form-select
  (:require
    [material-ui.core :as ui]
    [re-frame.core :as f]
    [schema.core :as s :include-macros true]
    [fractalify.utils :as u]
    [plumbing.core :as p]
    [clojure.walk :as w]
    [fractalify.main.schemas :as ch]
    [reagent.core :as r]
    [com.rpl.specter :as e]))

(def default-val-member :payload)
(def default-display-member :text)

(def style {:width "100%"})
(def MenuItems [{(s/cond-pre s/Str s/Keyword s/Num) s/Any}])

(s/defn parse-val [evt menu-items :- MenuItems val-member]
  "Hack to retrieve value types other than string, because e.target.value
  always returns string (no keywords)"
  (let [val (u/e-val evt)]
    (-> menu-items
        (->> (e/select [e/ALL val-member]))
        (#(zipmap % (u/range-count %)))
        w/stringify-keys
        (#(nth menu-items (% val)))
        (get val-member))))

(s/defn get-member-names [props]
  (p/letk [[{value-member default-val-member}] props
           [{display-member default-display-member}] props]
    [value-member display-member]))

(def Value (s/maybe s/Any #_ (s/cond-pre s/Str s/Keyword s/Num)))

(s/defn form-select
  ([_
    floating-label-text :- s/Str
    path :- ch/DbPath
    props :- {s/Keyword s/Any}]
    (let [val-member (first (get-member-names props))]
      (s/fn [value :- Value]
        [ui/select-field
         (r/merge-props
           {:value-member        default-val-member
            :display-member      default-display-member
            :floating-label-text floating-label-text
            :value               value
            :style               style
            :on-change           #(f/dispatch
                                   (u/concat-vec
                                     [:set-form-item]
                                     path
                                     [(parse-val % (:menu-items props) val-member)]))}
           props)]))))