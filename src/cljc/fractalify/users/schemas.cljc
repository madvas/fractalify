(ns fractalify.users.schemas
  (:require [schema.core :as s]
            [fractalify.main.schemas :as mch]
            [fractalify.utils :as u]))

(def o s/optional-key)

(def Email (s/pred (partial re-matches #"[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?")))
(def Username s/Str)
(def Password s/Str)
(def UserBio s/Str)

(def UsernameField
  {:username Username})

#_ (def UserRoles [(s/enum :admin :user)])
(def UserRoles [s/Keyword])

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
   :bio   UserBio})

(def UserForms
  {:login           LoginForm
   :join            JoinForm
   :forgot-password ForgotPassForm
   :change-password ChangePassForm
   :reset-password  ResetPassForm
   :edit-profile    EditProfileForm})

(def UsersSchema
  {(o :logged-user) (s/maybe UserMe)
   :forms           UserForms
   (o :user-detail) (s/conditional (complement (partial s/check UserOther)) UserOther :else UserMe)})


(def default-db
  {:forms (merge
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
