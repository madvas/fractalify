(ns fractalify.api.users.users-db
  (:require
    [monger.collection :as mc]
    [com.stuartsierra.component :as c]
    [plumbing.core :as p]
    [fractalify.utils :as u]
    [monger.joda-time]
    [clj-time.core :as t]
    [schema.core :as s]
    [clojurewerkz.scrypt.core :as sc]
    [monger.operators :refer :all])

  (:import [org.bson.types ObjectId]))

(def coll-name "users")
(def private-fields {:password 0 :salt 0})
(def public-fields (merge {:email 0} private-fields))

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
        pass-hash (u/hash-pass salt (:password user))]
    (mc/insert-and-return db coll-name (merge user {:_id      (ObjectId.)
                                                    :salt     salt
                                                    :password pass-hash
                                                    :created  (t/now)
                                                    :role     [:user]}))))

(s/defn get-user
  ([db where]
    (get-user db where {}))
  ([db where fields]
    (mc/find-one-as-map db coll-name where fields)))

(defn get-user-by-acc
  ([db username]
   (get-user-by-acc db username username))
  ([db username email]
   (get-user db {$or [{:username username}
                      {:email email}]})))

(s/defn verify-credentials [db {:keys [username password]}]
  (let [user (get-user-by-acc db username)
        submitted-pass password]
    (when user
      (p/letk [[password salt] user]
        (when (sc/verify (str salt submitted-pass) password)
          (select-keys user [:_id :username]))))))

