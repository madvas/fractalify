(ns fractalify.main.schemas
  (:require [schema.core :as s]
    #?@(:clj  [
            [clj-time.core :as m]]
        :cljs [[cljs-time.core :as m]
               [cljs.core]])))

(def o s/optional-key)

(s/defschema DbPath [(s/cond-pre s/Keyword s/Int)])

(s/defschema QueryParams {s/Keyword s/Any})

(s/defschema FormErros
  {(o :errors) {s/Keyword s/Any}})

(s/defschema Date #?(:clj  (s/pred #(or (satisfies? m/DateTimeProtocol %)
                                        (instance? java.util.Date %)))
                     :cljs (s/pred #(instance? js/Date %))))

(s/defschema ApiSendOpts
  {:api-route        s/Keyword
   (o :route-params) {s/Keyword s/Any}
   :params           {s/Keyword s/Any}
   :handler          s/Any
   :method           s/Keyword
   :error-handler    s/Any
   (o :error-undo?)  s/Bool})


(s/defschema ContactForm
  {:email   s/Str
   :text    s/Str
   :subject s/Str})

(s/defschema MainForms
  {:contact ContactForm})

(s/defschema MainSchema
  {:forms MainForms})

(defn list-response [item-type]
  {:items       [item-type]
   :total-items s/Int})