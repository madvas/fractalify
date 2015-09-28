(ns fractalify.users.schemas
  (:require [schema.core :as s]
            [fractalify.main.schemas :as mch]))

(def o s/optional-key)

(def Email (s/pred (partial re-matches #"[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?")))
(def Username s/Str)
(def Password s/Str)

(def UsernameField
  {:username Username})

(def UserRoles [(s/enum :admin :user)])

(def UserSession
  {:_id      (s/pred (partial instance? org.bson.types.ObjectId))
   :username Username
   :roles    UserRoles})

(def User
  {:id        s/Str
   :username  Username
   :gravatar  s/Str
   (o :email) Email
   (o :bio)   s/Str})

(def LoginForm
  {:username Username
   :password Password})

(def JoinForm
  {:username     Username
   :email        s/Str
   :password     Password
   :confirm-pass Password})

(def ForgotPassForm
  {:email s/Str})

(def ResetPassForm
  {:username Username
   :token    s/Str
   :new-pass Password})

(def ChangePassForm
  {:current-pass     Password
   :new-pass         Password
   :confirm-new-pass Password})

(def EditProfileForm
  {:email s/Str
   :bio   s/Str})

(def UserForms
  {:login           LoginForm
   :join            JoinForm
   :forgot-password ForgotPassForm
   :change-password ChangePassForm
   :edit-profile    EditProfileForm
   })

(def UsersSchema
  {(o :logged-user) User
   :forms           UserForms
   (o :user-detail) User})


(def default-db
  {:logged-user {:id       "123"
                 :username "madvas"
                 :email    "some@email.com"
                 :bio      "I am good"
                 :gravatar "http://www.gravatar.com/avatar/bfdb252fe9d0ab9759f41e3c26d7700e.jpg?s=50"}

   :forms       (merge
                  (mch/coerce-forms-with-defaults UserForms)
                  {:login {:username "HEHE" :password "abcdef"}})})