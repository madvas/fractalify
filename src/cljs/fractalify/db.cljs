(ns fractalify.db
  (:require [schema.core :as s :include-macros true]
            [fractalify.utils :as u]
            [fractalify.fractals.schemas :as fch]
            [fractalify.users.schemas :as uch]
            [fractalify.main.schemas :as mch]
            [instar.core :as i]))

(enable-console-print!)
(def o s/optional-key)

(def db-schema-base
  {(o :active-panel)   s/Keyword

   :users              uch/UsersSchema
   :fractals           fch/FractalsSchema

   (o :dialog-props)   {s/Keyword s/Any}
   (o :snackbar-props) {:message              s/Str
                        (o :action)           s/Str
                        (o :autoHideDuration) s/Int
                        (o :onActionTouchTap) s/Any}
   (o :route-params)   (s/maybe {s/Keyword s/Any})
   (o :queries)        {[s/Keyword] {(o :query-params) {s/Keyword s/Any}
                                     (o :loading?)     s/Bool
                                     (o :error)        s/Any}}})

(defn assoc-form-errors [db-schema]
  (i/transform db-schema [* :forms *] #(merge % mch/FormErros)))

(def db-schema
  (-> db-schema-base
      assoc-form-errors))

(defn valid? [db]
  (s/validate db-schema db))

(def default-db
  {:users    uch/default-db
   :fractals fch/default-db})
