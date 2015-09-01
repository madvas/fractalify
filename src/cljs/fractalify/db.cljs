(ns fractalify.db
  (:require [schema.core :as s :include-macros true]
            [fractalify.utils :as u]
            [workers.turtle.schemas :as turtle-schemas]))

(def o s/optional-key)


(def db-schema
  {(o :active-panel)        s/Keyword
   (o :user)                {:username s/Str
                             :email    s/Str
                             :bio      s/Str}
   :forms                   {(o :login)           {(o :user)     s/Str
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

                             (o :l-system)        turtle-schemas/LSystem}

   (o :l-system-generating) s/Bool
   (o :snackbar-props)      {:message              s/Str
                             (o :action)           s/Str
                             (o :autoHideDuration) s/Int
                             (o :onActionTouchTap) s/Any}
   (o :route-params)        (s/maybe {s/Keyword s/Any})})

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

(def plant1
  {:rules       [["F" "F[+F]F[-F]F"]]
   :angle       (u/round 25.7 2)
   :start       "F"
   :iterations  4
   :line-length 7
   :origin      {:x 300 :y 575}
   :start-angle 179})

(def koch-island
  {:rules       [["F" "F+FF-FF-F-F+F+FF-F-F+F+FF+FF-F"]]
   :angle       90
   :start       "F-F-F-F"
   :iterations  2
   :line-length 5
   :origin      {:x 300 :y 300}
   :start-angle 180})

(def koch-curve
  {:rules       [["F" "F+F-F-F+F"]]
   :angle       90
   :start       "-F"
   :iterations  4
   :line-length 36
   :origin      {:x 300 :y 300}
   :start-angle 167})

(def dragon-curve
  {:rules       [["X" "X+YF"] ["Y" "FX-Y"]]
   :angle       90
   :start       "FX"
   :iterations  12
   :line-length 6
   :origin      {:x 300 :y 300}
   :start-angle 90})

(def plant2 {:rules       [["F" "FF-[-F+F+F]+[+F-F-F]"]]
             :angle       22.5
             :start       "F"
             :iterations  4
             :line-length 10
             :origin      {:x 300 :y 550}
             :start-angle 180})

(def default-db
  {
   :user  {:username "madvas" :email "some@email.com" :bio "I am good"}

   :forms {:login           {:user "HEHE" :password "abcdef"}
           :forgot-password {:email "some@email"}
           ;:l-system        plant1
           ;:l-system        plant2
           ;:l-system        koch-curve
           :l-system        dragon-curve
           }})
