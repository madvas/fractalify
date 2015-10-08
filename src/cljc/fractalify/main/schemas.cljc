(ns fractalify.main.schemas
  (:require [schema.core :as s]
            [schema.coerce :as coerce]
            [fractalify.utils :as u]
    #?@(:clj  [
            [clj-time.core :as m]]
        :cljs [[cljs-time.core :as m]
               [cljs.core]])))

(def o s/optional-key)

(def DbPath [(s/cond-pre s/Keyword s/Int)])

(def QueryParams {s/Keyword s/Any})

(def FormErros
  {(o :errors) {s/Keyword s/Any}})

(def Date #?(:clj  (s/pred #(or (satisfies? m/DateTimeProtocol %)
                                (instance? java.util.Date %)))
             :cljs (s/pred #(instance? js/Date %))))

(s/defschema ApiSendOpts
  {:api-route        s/Keyword
   (o :route-params) {s/Keyword s/Any}
   :params           {s/Keyword s/Any}
   :handler          s/Any
   :method           s/Keyword
   :error-handler    s/Any})

(defn list-response [item-type]
  {:items       [item-type]
   :total-items s/Int})

(s/defn coerce-required-keys
  [schema :- {s/Any s/Any}]
  ((coerce/coercer
     schema
     (fn [schema]
       (when (u/array-map? schema)
         (fn [_]
           (->> schema
                (filter
                  #(and (keyword? (key %))))
                (u/map-values
                  #(cond
                    (= s/Str %) ""
                    (= s/Num %) 0
                    (= s/Int %) 0
                    (:default-value %) (:default-value %)
                    :else (println "error coerce:" (type %) %)))))))) {}))


(s/defn coerce-forms-with-defaults :- {s/Keyword s/Any}
  [forms-schemas]
  (apply merge
         (for [[k v] forms-schemas
               :let [form-name (if (instance? schema.core.OptionalKey k) (:k k) k)]]
           {form-name (coerce-required-keys v)})))