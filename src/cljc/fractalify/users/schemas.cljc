(ns fractalify.users.schemas
  (:require [schema.core :as s]
            [fractalify.main.schemas :as mch]
            [fractalify.utils :as u]
            [schema.experimental.abstract-map :as am]))

(def o s/optional-key)

(s/defschema Email (s/pred (partial re-matches #"[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?")))
(def Username s/Str)
(def Password s/Str)
(def UserBio s/Str)

(s/defschema UsernameField
  {:username Username})

(s/defschema UserRoles [(s/enum :admin :user)])

(s/defschema UserDb
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

(s/defschema UserId (u/select-key UserDb :id))

(s/defschema UserSession
  (select-keys UserDb [:id :username :roles]))

(def UserMe
  (dissoc UserDb
          :salt
          :password
          (o :reset-password-expire)
          (o :reset-password-token)))

(def UserOther (dissoc UserMe :email))

(s/defschema LoginForm
  {:username Username
   :password Password})

(s/defschema JoinForm
  {:username     Username
   :email        s/Str
   :password     Password
   :confirm-pass Password
   :bio          UserBio})

(s/defschema ForgotPassForm
  {:email s/Str})

(s/defschema ResetPassForm
  {:username Username
   :token    s/Str
   :new-pass Password})

(s/defschema ChangePassForm
  {:current-pass     Password
   :new-pass         Password
   :confirm-new-pass Password})

(s/defschema EditProfileForm
  {:email s/Str
   :bio   UserBio})

(s/defschema UserForms
  {:login           LoginForm
   :join            JoinForm
   :forgot-password ForgotPassForm
   :change-password ChangePassForm
   :edit-profile    EditProfileForm})

(s/defschema UsersSchema
  {(o :logged-user) (s/maybe UserMe)
   :forms           UserForms
   (o :user-detail) (s/conditional (complement (partial s/check UserOther)) UserOther :else UserMe)})


(def default-db
  {
   #_:logged-user #_{:id       "123"
                     :username "madvas"
                     :roles    []
                     :email    "some@email.com"
                     :bio      "I am good"
                     :gravatar "http://www.gravatar.com/avatar/bfdb252fe9d0ab9759f41e3c26d7700e.jpg?s=50"}

   :forms (merge
            (mch/coerce-forms-with-defaults UserForms)
            #?(:cljs
               (when goog.DEBUG {:login           {:username "admin" :password "111111"}
                                 :join            {:username     "newuser"
                                                   :email        "some@email.com"
                                                   :password     "111111"
                                                   :confirm-pass "111111"
                                                   :bio          ""}
                                 :forgot-password {:email "matus.lestan@gmail.com"}
                                 :change-password {:current-pass     "111111"
                                                   :new-pass         "111111"
                                                   :confirm-new-pass "111111"}})))})
