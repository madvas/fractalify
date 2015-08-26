(ns fractalify.db
  (:require [schema.core :as s :include-macros true]
            [fractalify.utils :as u]))

(def o s/optional-key)

(def db-schema
  {(o :active-panel)   s/Keyword
   (o :user)           {:username s/Str
                        :email    s/Str
                        :bio      s/Str}
   :forms              {(o :login)           {(o :user)     s/Str
                                              (o :password) s/Str
                                              }

                        (o :join)            {(o :username)     s/Str
                                              (o :email)        s/Str
                                              (o :password)     s/Str
                                              (o :confirm-pass) s/Str}

                        (o :forgot-password) {(o :email) s/Str}

                        (o :change-password) {(o :current-pass)     s/Str
                                              (o :new-pass)         s/Str
                                              (o :confirm-new-pass) s/Str}

                        (o :edit-profile)    {(o :email) s/Str
                                              (o :bio)   s/Str}

                        (o :l-system)        {(o :rules)       (s/maybe [[(s/one s/Str "rule-source")
                                                                          (s/one s/Str "rule-product")]])
                                              (o :start)       s/Str
                                              (o :angle)       s/Num
                                              (o :constants)   #{s/Str}
                                              (o :iterations)  (s/pred pos?)
                                              (o :line-length) s/Num
                                              (o :start-angle) s/Num
                                              (o :origin)      {:x s/Num :y s/Num}
                                              (o :result-cmds) s/Str}
                        }

   (o :snackbar-props) {:message              s/Str
                        (o :action)           s/Str
                        (o :autoHideDuration) s/Int
                        (o :onActionTouchTap) s/Any}
   (o :route-params)   (s/maybe {s/Keyword s/Any})})

(defn assoc-form-validaton-properties [db-schema]
  (reduce (fn [forms form]
            (assoc-in forms [:forms form (o :errors)] {s/Any s/Any}))
          db-schema
          (keys (:forms db-schema))))


(defn create-db-schema [db-schema]
  (-> db-schema
      assoc-form-validaton-properties))

(defn valid? [db]
  (s/validate (create-db-schema db-schema) db))


(def default-db
  {
   :user  {:username "madvas" :email "some@email.com" :bio "I am good"}

   :forms {:login           {:user "HEHE" :password "abcdef"}
           :forgot-password {:email "some@email"}
           :l-system        {:rules       [["F" "F[+F]F[-F]F"]]
                             #_[["a" "b-a-b"]
                                ["b" "a+b+a"]]
                             :angle       (u/round 2 25.7)
                             :start       "F"
                             :iterations  3
                             :line-length 7
                             :origin      {:x 250 :y 250}
                             :start-angle 180}}})
