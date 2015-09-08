(ns fractalify.users.schemas
  (:require [schema.core :as s]))

(def o s/optional-key)

(def User
  {:username  s/Str
   :gravatar  s/Str
   (o :email) s/Str
   (o :bio)   s/Str})

(def UsersSchema
  {(o :logged-user) User
   :forms           {(o :login)           {(o :user)     s/Str
                                           (o :password) s/Str}

                     (o :join)            {(o :username)     s/Str
                                           (o :email)        s/Str
                                           (o :password)     s/Str
                                           (o :confirm-pass) s/Str}

                     (o :forgot-password) {(o :email) s/Str}

                     (o :change-password) {(o :current-pass)     s/Str
                                           (o :new-pass)         s/Str
                                           (o :confirm-new-pass) s/Str}

                     (o :edit-profile)    {(o :email) s/Str
                                           (o :bio)   s/Str}}})