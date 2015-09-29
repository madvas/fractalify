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
    [fractalify.api.api :as api]
    [instar.core :as i]
    [fractalify.api.users.users-db :as udb]
    [fractalify.users.schemas :as uch])
  (:import (org.bson.types ObjectId)))

(def coll "fractals")

(defrecord FractalsDb []
  c/Lifecycle
  (start [this]
    (p/letk [[db] (:db-server this)]
      (when-not (mc/exists? db coll)
        (mc/create db coll {}))
      (mc/ensure-index db coll (array-map :star-count 1 :created 1))
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
  (api/populate db udb/coll :author :username uch/UserOther fractals))

(s/defn fractal-db->cljs [fractal]
  (when fractal
    (-> fractal
        (transform-rules-cmds u/vec->indexed-map)
        (i/transform [:l-system :cmds * 1] keyword)
        (assoc :starred-by-me (starred-by-me? (:stars fractal)))
        (dissoc :stars)
        (api/db->cljs fch/PublishedFractal))))

(s/defn fractal-insert-and-return [db fractal author]
  (api/insert-and-return db coll (merge (fractal-cljs->db fractal)
                                        {:created    (t/now)
                                         :author     (:username author)
                                         :stars      []
                                         :star-count 0
                                         :comments   []})
                         fch/PublishedFractal
                         (p/fn->> (populate-author db)
                                  fractal-db->cljs)))

(def parse-fractals-req
  (u/create-str-coercer fch/FractalListForm))

(defn fractal-count [db]
  (mc/count db coll))

(s/defn fractal-get-by-id [db fractal-id]
  (-> (mc/find-map-by-id db coll (ObjectId. fractal-id))
      (->> (populate-author db))
      fractal-db->cljs))

(s/defn get-fractals
  [db params]
  (p/letk [[{page 1}
            {limit 10}
            {sort :created}
            {sort-dir -1}
            {username nil}] (parse-fractals-req params)]
    (->> (q/with-collection db coll
                            (q/find (when username {:author username}))
                            (q/fields {:comments 0})
                            (q/sort (array-map sort sort-dir))
                            (q/paginate :page page :per-page limit))
         (populate-author db)
         (map fractal-db->cljs))))

