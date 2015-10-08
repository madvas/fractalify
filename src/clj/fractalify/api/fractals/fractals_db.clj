(ns fractalify.api.fractals.fractals-db
  (:require
    [monger.collection :as mc]
    [com.stuartsierra.component :as c]
    [plumbing.core :as p]
    [fractalify.utils :as u]
    [monger.joda-time]
    [clj-time.core :as t]
    [monger.query :as q]
    [schema.core :as s]
    [fractalify.fractals.schemas :as fch]
    [cemerick.friend :as frd]
    [fractalify.api.api :as a]
    [instar.core :as i]
    [fractalify.api.users.users-db :as udb]
    [fractalify.users.schemas :as uch]
    [monger.operators :refer :all])
  (:import (org.bson.types ObjectId)))

(def coll "fractals")
(def coll-comments "fractals-comments")

(defrecord FractalsDb []
  c/Lifecycle
  (start [this]
    (p/letk [[db] (:db-server this)]
      (when-not (mc/exists? db coll)
        (mc/create db coll {}))
      (mc/ensure-index db coll (array-map :star-count 1 :created 1))
      (when-not (mc/exists? db coll-comments)
        (mc/create db coll-comments {}))
      (mc/ensure-index db coll-comments (array-map :fractal 1 :created 1))
      this))

  (stop [this]
    this))

(defn new-fractals-db []
  (map->FractalsDb {}))

(defn transform-rules-cmds [fractal f]
  (i/transform fractal [:l-system #"rules|cmds"] f))

(s/defn fractal-cljs->db [fractal]
  (transform-rules-cmds fractal u/indexed-map->vec))

(defn starred-by-me? [stars]
  (p/if-letk [[username] (frd/current-authentication)]
    (contains? (set stars) username)
    false))

(defn populate-author [db fractals]
  (a/populate db udb/coll :author :username uch/UserOther fractals))

(s/defn fractal-db->cljs [fractal]
  (when fractal
    (-> fractal
        (transform-rules-cmds u/vec->indexed-map)
        (i/transform [:l-system :cmds * 1] keyword)
        (assoc :starred-by-me (starred-by-me? (:stars fractal)))
        (dissoc :stars)
        (a/db->cljs fch/PublishedFractal))))

(s/defn fractal-insert-and-return [db fractal author]
  (a/insert-and-return db coll (merge (fractal-cljs->db fractal)
                                      {:created    (t/now)
                                       :author     (:username author)
                                       :stars      []
                                       :star-count 0})
                       fch/PublishedFractal
                       (p/fn->> (populate-author db)
                                fractal-db->cljs)))

(defn fractal-count [db]
  (mc/count db coll))

(s/defn fractal-get-by-id [db fractal-id]
  (when (ObjectId/isValid fractal-id)
    (when-let [fractal (mc/find-map-by-id db coll (ObjectId. fractal-id))]
      (-> fractal
          (->> (populate-author db))
          fractal-db->cljs))))

(defn fractal-delete-by-id [db fractal-id]
  (when (ObjectId/isValid fractal-id)
    (let [id (ObjectId. fractal-id)]
      (mc/remove-by-id db coll id)
      (mc/remove db coll-comments {:fractal id}))))

(s/defn get-fractals
  [db params]
  (p/letk [[{page 1}
            {limit 10}
            {sort :created}
            {sort-dir -1}
            {username nil}] (u/coerce-str params fch/FractalListForm)]
    (->> (q/with-collection db coll
                            (q/find (when username {:author username}))
                            (q/sort (array-map sort sort-dir))
                            (q/paginate :page page :per-page limit))
         (populate-author db)
         (map fractal-db->cljs))))

(defn star-fractal
  ([db fractal-id user] (star-fractal db fractal-id user false))
  ([db fractal-id user unstar?]
   (when (ObjectId/isValid fractal-id)
     (let [fractal-id (ObjectId. fractal-id)
           op (if unstar? $pull $addToSet)]
       (let [fractal (mc/find-and-modify db coll {:_id fractal-id}
                                         {op {:stars (:username user)}}
                                         {:return-new true :fields [:stars]})]
         (mc/update-by-id db coll fractal-id
                          {$set {:star-count (count (:stars fractal))}}))))))


(s/defn comment-db->cljs [comment]
  (when comment
    (-> comment
        (update :fractal str)
        (a/db->cljs fch/Comment))))


(defn get-comment-by-id [db comment-id]
  (when (ObjectId/isValid comment-id)
    (when-let [comment (mc/find-map-by-id db coll-comments (ObjectId. comment-id))]
      (-> comment
          (->> (populate-author db))
          comment-db->cljs))))

(defn get-comments [db fractal-id]
  (when (ObjectId/isValid fractal-id)
    (->> (q/with-collection db coll-comments
                            (q/find {:fractal (ObjectId. fractal-id)})
                            (q/sort (array-map :created 1)))
         (populate-author db)
         (map comment-db->cljs))))

(defn comment-insert-and-return [db comment fractal-id author]
  (when (ObjectId/isValid fractal-id)
    (a/insert-and-return db coll-comments (merge comment
                                                 {:created (t/now)
                                                  :author  (:username author)
                                                  :fractal (ObjectId. fractal-id)})
                         fch/Comment
                         (p/fn->> (populate-author db)
                                  comment-db->cljs))))

(defn comment-delete-by-id [db comment-id]
  (when (ObjectId/isValid comment-id)
    (mc/remove-by-id db coll-comments (ObjectId. comment-id))))