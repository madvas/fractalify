(ns fractalify.components.form
  (:require [fractalify.main.schemas :as ch]
            [schema.core :as s :include-macros true]
            [re-frame.core :as f]
            [fractalify.utils :as u]))

(s/defn form
  [module :- s/Keyword
   form-name :- s/Keyword
   contents]
  (let [form-vals (f/subscribe [:form-data module form-name])
        form-errs (f/subscribe [:form-errors module form-name])]
    (fn []
      [:form {:on-submit (constantly false)}
       [contents @form-vals (not (empty? @form-errs))]])))