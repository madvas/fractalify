(ns fractalify.api.users.users-generator
  (:require [fractalify.db-generator :only [Generator]]
            [com.stuartsierra.component :as c]
            [fractalify.utils :as u]
            [clojure.test.check.generators :as gen]
            [plumbing.core :as p]
            [monger.collection :as mc]
            [fractalify.api.users.users-db :as udb]))

(defn gen-user
  ([] (gen-user nil))
  ([user]
   (merge
     {:username (u/gen-str 6)
      :email    (u/gen-email)
      :password (u/gen-str 6)
      :bio      (u/gen-sentence 10 3 10)}
     user)))

(def admin
  {:username "admin"
   :email    "matus.lestan@gmail.com"
   :password "111111"})

(def some-user
  {:username "matus"
   :email    "somemai@gmail.com"
   :password "111111"})

(defrecord UsersGenerator []
  c/Lifecycle
  (start [this]
    (p/letk [[db] (:db-server this)]
      (mc/remove db udb/coll-name)
      (let [admin (udb/add-user db (gen-user admin))]
        (udb/add-admin-role db (:_id admin)))
      (udb/add-user db (gen-user some-user))
      (assoc this :users
                  (doall
                    (take 10 (repeatedly #(udb/add-user db (gen-user))))))))

  (stop [this]
    (dissoc this :users)))

(def new-users-generator ->UsersGenerator)
