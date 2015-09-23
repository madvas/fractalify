(ns fractalify.users.schemas
  (:require [schema.core :as s]))

(def o s/optional-key)

(def Email (s/pred (partial re-matches #"[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?")))

(def User
  {:id        s/Str
   :username  s/Str
   :gravatar  s/Str
   (o :email) Email
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
                                           (o :bio)   s/Str}}
   (o :user-detail) User})

(def default-db
  {:logged-user {:id       "123"
                 :username "madvas"
                 :email    "some@email.com"
                 :bio      "I am good"
                 :gravatar "http://www.gravatar.com/avatar/bfdb252fe9d0ab9759f41e3c26d7700e.jpg?s=50"}

   :forms       {:login {:user "HEHE" :password "abcdef"}}})