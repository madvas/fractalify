(ns fractalify.components.form-select
  (:require
    [material-ui.core :as ui]
    [re-frame.core :as f]
    [schema.core :as s :include-macros true]
    [fractalify.utils :as u]
    [plumbing.core :as p]
    [instar.core :as i]
    [clojure.walk :as w]))

(def params-schema [(s/cond-pre s/Keyword s/Int s/Str)])

(def default-val-member "payload")
(def default-display-member "text")
(def style {:width "100%"})


(s/defn parse-val :- (s/cond-pre s/Str s/Keyword s/Num)
  [evt
   menu-items :- [{s/Str s/Any}]
   val-member :- s/Str]
  "Hack to retrieve value types other than string, because e.target.value
  always returns string (no keywords)"
  (let [val (u/e-val evt)]
    (-> menu-items
        (i/get-values-in-paths [* val-member])
        (#(zipmap % (u/range-count %)))
        w/stringify-keys
        (#(nth menu-items (% val)))
        (get val-member))))

(s/defn get-member-names :- [(s/one s/Str "value-member")
                             (s/one s/Str "display-member")]
  [props]
  (p/letk [[{value-member default-val-member}] props
           [{display-member default-display-member}] props]
    [value-member display-member]))

(s/defn default-menu-items-fn :- [{s/Str s/Any}]
  [menu-items props]
  (map #(zipmap (get-member-names props) %) menu-items))

(s/defn form-select
  ([form-item-path menu-items-sub props]
    (form-select form-item-path menu-items-sub default-menu-items-fn props))
  ([form-item-path :- params-schema
    menu-items-sub :- [s/Any]
    menu-items-fn
    props :- {s/Keyword s/Any}]
    (let [value (f/subscribe (into [:get-form-item] form-item-path))
          menu-items-vals (f/subscribe menu-items-sub)
          val-member (first (get-member-names props))]
      (fn []
        (let [menu-items (vec (menu-items-fn @menu-items-vals props))]
          [ui/select-field
           (merge {:menu-items     menu-items
                   :value-member   default-val-member
                   :display-member default-display-member
                   :value          @value
                   :style          style
                   :on-change      #(f/dispatch
                                     (u/concat-vec
                                       [:set-form-item]
                                       form-item-path
                                       [(parse-val % menu-items val-member)]))}
                  props)])))))