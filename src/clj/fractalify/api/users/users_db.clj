(ns fractalify.api.users.users-db
  (:require
    [monger.collection :as mc]
    [com.stuartsierra.component :as c]
    [plumbing.core :as p]
    [fractalify.utils :as u]
    [monger.joda-time]
    [clj-time.core :as t]
    [clojurewerkz.scrypt.core :as sc]
    [schema.core :as s])
  (:import [org.bson.types ObjectId]))

(def coll-name "users")

(defrecord UsersDb []
  c/Lifecycle
  (start [this]
    (p/letk [[db] (:db-server this)]
      (when-not (mc/exists? db coll-name)
        (mc/create db coll-name {}))
      (mc/ensure-index db coll-name (array-map :username 1) {:unique true})
      (assoc this :coll-name coll-name)))

  (stop [this]
    (dissoc this :coll-name)))

(defn new-users-db []
  (map->UsersDb {}))

(s/defn add-user [db user]
  (let [salt (u/gen-str 16)
        pass-hash (sc/encrypt (str salt (:password user)) 16384 8 1)]
    (mc/insert-and-return db coll-name (merge user {:_id      (ObjectId.)
                                                    :salt     salt
                                                    :password pass-hash
                                                    :created  (t/now)
                                                    :role     [:user]}))))

