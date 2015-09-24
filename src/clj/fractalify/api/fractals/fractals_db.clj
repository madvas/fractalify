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
    [fractalify.fractals.schemas :as fch])
  (:import [org.bson.types ObjectId]))

(def coll-name "fractals")

(defrecord FractalsDb []
  c/Lifecycle
  (start [this]
    (p/letk [[db] (:db-server this)]
      (when-not (mc/exists? db coll-name)
        (mc/create db coll-name {}))
      (mc/ensure-index db coll-name (array-map :star-count 1 :created 1))
      this))

  (stop [this]
    this))

(defn new-fractals-db []
  (map->FractalsDb {}))

(s/defn add-fractal [db fractal author]
  (mc/insert-and-return db coll-name (merge fractal
                                            {:_id           (ObjectId.)
                                             :created       (t/now)
                                             :author        (u/select-key author :username)
                                             :stars         []
                                             :star-count    0
                                             :comments      []
                                             })))

(def parse-fractals-req
  (u/create-str-coercer fch/FractalsQueryParams))

(s/defn get-fractals [db params]
  (p/letk [[{page 1}
            {limit 10}
            {sort :created}
            {sort-dir -1}
            {username nil}] (u/p "para:" (parse-fractals-req params))]
    (q/with-collection db coll-name
                       (q/find (when username {:author {:username username}}))
                       (q/fields {:comments 0 :stars 0})
                       (q/sort (array-map sort sort-dir))
                       (q/paginate :page page :per-page limit))))

