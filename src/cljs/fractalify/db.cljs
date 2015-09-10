(ns fractalify.db
  (:require [schema.core :as s :include-macros true]
            [fractalify.utils :as u]
            [fractalify.fractals.schemas :as fractals-schemas]
            [fractalify.users.schemas :as users-schemas]
            [fractalify.main.schemas :as main-schemas]
            [instar.core :as i]))

(def o s/optional-key)

(def db-schema
  {(o :active-panel)   s/Keyword

   (o :users)          users-schemas/UsersSchema
   (o :fractals)       fractals-schemas/FractalsSchema

   (o :dialog-props)   {s/Keyword s/Any}
   (o :snackbar-props) {:message              s/Str
                        (o :action)           s/Str
                        (o :autoHideDuration) s/Int
                        (o :onActionTouchTap) s/Any}
   (o :route-params)   (s/maybe {s/Keyword s/Any})})

(defn assoc-form-errors [db-schema]
  (i/transform db-schema [* :forms *] #(merge % main-schemas/FormErros)))

(defn create-db-schema [db-schema]
  (-> db-schema
      assoc-form-errors))

(defn valid? [db]
  (s/validate (create-db-schema db-schema) db))

(def dragon-curve
  {:l-system {:rules       {1 ["X" "X+YF"]
                            2 ["Y" "FX-Y"]}
              :angle       90
              :start       "FX"
              :iterations  12
              :line-length 6
              :origin      {:x 300 :y 300}
              :start-angle 90
              :cmds        {1 ["F" :forward]
                            2 ["+" :left]
                            3 ["-" :right]
                            4 ["[" :push]
                            5 ["]" :pop]}}
   :canvas   {:bg-color   ["#FFF" 100]
              :size       600
              :color      ["#000" 100]
              :line-width 1}})

(def default-db
  {:users    {:logged-user {:username "madvas"
                            :email    "some@email.com"
                            :bio      "I am good"
                            :gravatar "http://www.gravatar.com/avatar/bfdb252fe9d0ab9759f41e3c26d7700e.jpg?s=50"}

              :forms       {:login {:user "HEHE" :password "abcdef"}}}
   :fractals {:forms    (merge {} dragon-curve)
              :all-cmds {:forward "Forward"
                         :left    "Rotate Left"
                         :right   "Rotate Right"
                         :push    "Push Position"
                         :pop     "Pop Position"
                         :default "No Action"}}})
