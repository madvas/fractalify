(ns fractalify.db
  (:require [schema.core :as s :include-macros true]
            [fractalify.utils :as u]
            [fractalify.fractals.schemas :as fractals-schemas]
            [fractalify.users.schemas :as users-schemas]
            [fractalify.main.schemas :as main-schemas]
            [instar.core :as i]))

(def o s/optional-key)

(def db-schema
  {(o :active-panel)   s/Keyword

   (o :users)          users-schemas/UsersSchema
   (o :fractals)       fractals-schemas/FractalsSchema

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
  (i/transform db-schema [* :forms *] #(merge % main-schemas/FormErros)))

(defn create-db-schema [db-schema]
  (-> db-schema
      assoc-form-errors))

(defn valid? [db]
  (s/validate (create-db-schema db-schema) db))

(def default-db
  {:users    users-schemas/default-db
   :fractals fractals-schemas/default-db})
