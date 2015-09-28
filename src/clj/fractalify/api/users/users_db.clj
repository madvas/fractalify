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
    [monger.operators :refer :all]
    [clj-time.core :as m]
    [fractalify.users.schemas :as uch]
    [schema.coerce :as coerce]
    [fractalify.api.api :as api])

  (:import [org.bson.types ObjectId]))

(def coll-name "users")
(def private-fields {:password 0 :salt 0 :reset-password-expire 0 :reset-password-token 0})
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


(defn user-db->cljs [user]
  (-> user

      api/_id->id))

(defn gen-password [password]
  (let [salt (u/gen-str 16)
        pass-hash (u/hash-pass salt password)]
    {:salt salt :password pass-hash}))

(s/defn add-user [db user]
  (mc/insert-and-return db coll-name
                        (merge user
                               {:_id     (ObjectId.)
                                :created (t/now)
                                :roles   #{:user}}
                               (gen-password (:password user)))))

(s/defn get-user
  ([db where]
    (get-user db where {}))
  ([db where fields]
    (mc/find-one-as-map db coll-name where fields)))

(defn update-user [db where what]
  (mc/update db coll-name where {$set what}))

(defn get-user-by-acc
  ([db username]
   (get-user-by-acc db username username))
  ([db username email]
   (get-user db {$or [{:username username}
                      {:email email}]})))

(def coerce-user
  (coerce/coercer uch/UserSession coerce/json-coercion-matcher))

(s/defn verify-credentials :- (s/maybe uch/UserSession)
  [db {:keys [username password]}]
  (let [user (get-user-by-acc db username)
        submitted-pass password]
    (when user
      (p/letk [[password salt] user]
        (when (sc/verify (str salt submitted-pass) password)
          (coerce-user (select-keys user [:_id :username :roles])))))))

(s/defn create-reset-token :- s/Str
  [db user-id]
  (let [token (u/gen-str 20)
        expire-date (m/plus (m/now) (m/weeks 1))]
    (mc/update-by-id db coll-name user-id {$set
                                           {:reset-password-token  token
                                            :reset-password-expire expire-date}})
    token))

(defn get-user-by-reset-token
  [db username token]
  (get-user db {$and [{:username username}
                      {:reset-password-token token}
                      {:reset-password-expire {$gte (m/now)}}]} public-fields))

(defn set-new-password
  [db username new-pass]
  (update-user db {:username username} (merge (gen-password new-pass)
                                              {:reset-password-token  nil
                                               :reset-password-expire nil})))

(defn add-admin-role [db user-id]
  (mc/update-by-id db coll-name user-id {$addToSet
                                         {:roles :admin}}))