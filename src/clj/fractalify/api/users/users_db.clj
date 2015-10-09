(ns fractalify.api.users.users-db
  (:require
    [monger.collection :as mc]
    [com.stuartsierra.component :as c]
    [plumbing.core :as p]
    [fractalify.utils :as u]
    [monger.json]
    [monger.joda-time]
    [clj-time.core :as t]
    [schema.core :as s]
    [clojurewerkz.scrypt.core :as sc]
    [monger.operators :refer :all]
    [clj-time.core :as m]
    [fractalify.users.schemas :as uch]
    [fractalify.api.api :as api]
    [digest])
  (:import (org.bson.types ObjectId)))

(def coll "users")

(defrecord UsersDb []
  c/Lifecycle
  (start [this]
    (p/letk [[db] (:db-server this)]
      (when-not (mc/exists? db coll)
        (mc/create db coll {}))
      (mc/ensure-index db coll (array-map :username 1) {:unique true})
      (assoc this :coll-name coll)))

  (stop [this]
    (dissoc this :coll-name)))

(defn new-users-db []
  (map->UsersDb {}))

(defn gen-password [password]
  (let [salt (u/gen-str 16)
        pass-hash (u/hash-pass salt password)]
    {:salt salt :password pass-hash}))

(defn get-user
  ([db where]
   (get-user db where uch/UserDb))
  ([db where schema]
   (-> (mc/find-one-as-map db coll where (api/schema->fields schema))
       (api/db->cljs schema))))

(s/defn user-insert-and-return
  ([db user] (user-insert-and-return db user uch/UserOther))
  ([db user schema]
    (api/insert-and-return db coll (merge user
                                          {:created  (t/now)
                                           :roles    [:user]
                                           :gravatar (digest/md5 (:email user))}
                                          (gen-password (:password user)))
                           schema)))

(defn update-user [db where what]
  (let [fields (if-let [email (:email what)]
                 (assoc what :gravatar (digest/md5 email))
                 what)]
    (mc/update db coll where {$set fields})))

(defn get-user-by-acc
  ([db username schema]
   (get-user-by-acc db username username schema))
  ([db username email schema]
   (get-user db {$or [{:username username}
                      {:email email}]} schema)))

(s/defn verify-credentials :- (s/maybe uch/UserSession)
  [db {:keys [username password]}]
  (let [user (get-user-by-acc db username uch/UserDb)
        submitted-pass password]
    (when user
      (p/letk [[password salt] user]
        (when (sc/verify (str salt submitted-pass) password)
          (u/select-req-keys user uch/UserSession))))))

(s/defn create-reset-token :- s/Str
  [db user-id]
  (let [token (u/gen-str 20)
        expire-date (m/plus (m/now) (m/weeks 1))]
    (mc/update-by-id db coll (ObjectId. user-id) {$set
                                                  {:reset-password-token  token
                                                   :reset-password-expire expire-date}})
    token))

(defn get-user-by-reset-token
  [db username token]
  (get-user db {$and [{:username username}
                      {:reset-password-token token}
                      {:reset-password-expire {$gte (m/now)}}]} uch/UserMe))

(defn set-new-password
  [db username new-pass]
  (update-user db {:username username} (merge (gen-password new-pass)
                                              {:reset-password-token  nil
                                               :reset-password-expire nil})))

(defn add-admin-role [db user-id]
  (mc/update-by-id db coll (ObjectId. user-id) {$addToSet
                                                {:roles :admin}}))