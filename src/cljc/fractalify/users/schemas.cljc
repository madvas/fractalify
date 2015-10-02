(ns fractalify.users.schemas
  (:require [schema.core :as s]
            [fractalify.main.schemas :as mch]
            [fractalify.utils :as u]))

(def o s/optional-key)

(s/defschema Email (s/pred (partial re-matches #"[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?")))
(def Username s/Str)
(def Password s/Str)
(def UserBio s/Str)

(def UsernameField
  {:username Username})

(def UserRoles [(s/enum :admin :user)])

(def UserDb
  {:id                        s/Str
   :username                  Username
   :roles                     UserRoles
   :created                   mch/Date
   :gravatar                  s/Str
   :salt                      s/Str
   :password                  s/Str
   :email                     Email
   :bio                       UserBio
   (o :reset-password-expire) (s/maybe mch/Date)
   (o :reset-password-token)  (s/maybe s/Str)})

(def UserId (u/select-key UserDb :id))

(def UserSession
  (select-keys UserDb [:id :username :roles]))

(def UserMe
  (dissoc UserDb
          :salt
          :password
          (o :reset-password-expire)
          (o :reset-password-token)))

(def UserOther (dissoc UserMe :email))

(def LoginForm
  {:username Username
   :password Password})

(def JoinForm
  {:username     Username
   :email        s/Str
   :password     Password
   :confirm-pass Password
   :bio          UserBio})

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
  {(o :logged-user) (s/maybe UserMe)
   :forms           UserForms
   (o :user-detail) UserDb})


(def default-db
  {
   #_ :logged-user #_ {:id       "123"
                 :username "madvas"
                 :roles []
                 :email    "some@email.com"
                 :bio      "I am good"
                 :gravatar "http://www.gravatar.com/avatar/bfdb252fe9d0ab9759f41e3c26d7700e.jpg?s=50"}

   :forms       (merge
                  (mch/coerce-forms-with-defaults UserForms)
                  {:login {:username "HEHE" :password "abcdef"}})})