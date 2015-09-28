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
    [fractalify.api.users.users-db :as udb])
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

(defn transform-rules-cmds [fractal f]
  (i/transform fractal [:l-system #"rules|cmds"] f))

(s/defn fractal-cljs->db [fractal]
  (transform-rules-cmds fractal u/indexed-map->vec))

(defn starred-by-me? [stars]
  (p/if-letk [[_id] (frd/current-authentication)]
    (contains? (set stars) _id)
    false))

(s/defn fractal-db->cljs [fractal]
  (-> fractal
      (transform-rules-cmds u/vec->indexed-map)
      (i/transform [:l-system :cmds * 1] keyword)
      (assoc :starred-by-me (starred-by-me? (:stars fractal)))
      (dissoc :stars)
      api/_id->id))

(s/defn add-fractal [db fractal author]
  (mc/insert-and-return db coll-name (merge (fractal-cljs->db fractal)
                                            {:_id        (ObjectId.)
                                             :created    (t/now)
                                             :author     (:_id author)
                                             :stars      []
                                             :star-count 0
                                             :comments   []
                                             })))

(def parse-fractals-req
  (u/create-str-coercer fch/FractalListForm))

(defn fractal-count [db]
  (mc/count db coll-name))

(s/defn get-fractal [db fractal-id]
  (-> (mc/find-by-id db coll-name fractal-id)
      fractal-db->cljs))

(s/defn get-fractals
  [db params]
  (p/letk [[{page 1}
            {limit 10}
            {sort :created}
            {sort-dir -1}
            {username nil}] (parse-fractals-req params)]
    (u/p "here:" (->> (q/with-collection db coll-name
                             (q/find (when username {:author {:username username}}))
                             (q/fields {:comments 0})
                             (q/sort (array-map sort sort-dir))
                             (q/paginate :page page :per-page limit))
          (api/populate db udb/coll-name :author udb/public-fields)
          (map fractal-db->cljs)))))

