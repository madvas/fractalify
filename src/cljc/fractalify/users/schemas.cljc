(ns fractalify.users.schemas
  (:require [schema.core :as s]))

(def o s/optional-key)

(def Email (s/pred (partial re-matches #"[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?")))
(def Username s/Str)


(def User
  {:id        s/Str
   :username  s/Str
   :gravatar  s/Str
   (o :email) Email
   (o :bio)   s/Str})

(def LoginForm
  {(o :username) s/Str
   (o :password) s/Str})

(def JoinForm
  {(o :username)     s/Str
   (o :email)        s/Str
   (o :password)     s/Str
   (o :confirm-pass) s/Str})

(def ForgotPassForm
  {(o :email) s/Str})

(def ChangePassForm
  {(o :current-pass)     s/Str
   (o :new-pass)         s/Str
   (o :confirm-new-pass) s/Str})

(def EditProfileForm
  {(o :email) s/Str
   (o :bio)   s/Str})

(def UsersSchema
  {(o :logged-user) User
   :forms           {(o :login)           LoginForm
                     (o :join)            JoinForm
                     (o :forgot-password) ForgotPassForm
                     (o :change-password) ChangePassForm
                     (o :edit-profile)    EditProfileForm}
   (o :user-detail) User})

(def default-db
  {:logged-user {:id       "123"
                 :username "madvas"
                 :email    "some@email.com"
                 :bio      "I am good"
                 :gravatar "http://www.gravatar.com/avatar/bfdb252fe9d0ab9759f41e3c26d7700e.jpg?s=50"}

   :forms       {:login {:username "HEHE" :password "abcdef"}}})