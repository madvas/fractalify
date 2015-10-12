(ns fractalify.db
  (:require [schema.core :as s :include-macros true]
            [fractalify.main.db :as mdb]
            [fractalify.users.db :as udb]
            [fractalify.fractals.db :as fdb]
            [com.rpl.specter :as e]
            [fractalify.main.schemas :as mch]
            [fractalify.users.schemas :as uch]
            [fractalify.fractals.schemas :as fch]))

(enable-console-print!)
(def o s/optional-key)

(def db-schema-base
  {(o :active-panel)   s/Keyword

   :main               mch/MainSchema
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
  (reduce #(e/transform [%2 :forms e/ALL e/LAST]
                        (partial merge mch/FormErros) %1) db-schema [:main :users :fractals]))

(def db-schema
  (-> db-schema-base
      assoc-form-errors))

(defn valid? [db]
  (s/validate db-schema db))

(def default-db
  {:main     mdb/default-db
   :users    udb/default-db
   :fractals fdb/default-db})
