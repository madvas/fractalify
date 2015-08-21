(ns fractalify.db
  (:require [schema.core :as s :include-macros true]
            [fractalify.utils :as u]))

(def o s/optional-key)

(def db-schema
  {(o :active-panel) s/Keyword
   (o :user)         {:username s/Str
                      :email    s/Str}
   :forms            {(o :login) {(o :user)     s/Str
                                  (o :password) s/Str
                                  }

                      (o :join)  {(o :username)     s/Str
                                  (o :email)        s/Str
                                  (o :password)     s/Str
                                  (o :confirm-pass) s/Str}}})

(defn assoc-form-validaton-properties [db-schema]
  (reduce (fn [forms form]
            (assoc-in forms [:forms form (o :errors)] {s/Keyword s/Str}))
          db-schema
          (keys (:forms db-schema))))


(defn create-db-schema [db-schema]
  (-> db-schema
      assoc-form-validaton-properties))

(defn valid? [db]
  (s/validate (create-db-schema db-schema) db))


(def default-db
  {:forms {:login {:user "Hello" :password "abc"}}})
