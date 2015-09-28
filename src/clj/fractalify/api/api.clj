(ns fractalify.api.api
  (:require [schema.core :as s]
            [cemerick.friend :as frd]
            [fractalify.utils :as u]
            [plumbing.core :as p]
            [clojure.set :as set]
            [monger.collection :as mc]
            [monger.operators :as mop]))

(defn malformed-params? [schema params]
  (fn [_] (s/check schema params)))

(def base-resource
  {:available-media-types ["application/edn"]})

(defn admin? [_]
  (p/when-letk [[roles] (frd/current-authentication)]
    (contains? (u/p "roles:" (set roles)) :admin)))

(defn _id->id [x]
  (-> x
      (set/rename-keys {:_id :id})
      (update :id str)))

(defn populate "Populates the given docs sequence by looking up the 'foreign key' as an :_id in `foreign-coll`.
`foreign-path` can be either a single key or a sequence of keys (as in get-in)
Assumes the foreign keys are ObjectIds or coercable to objectIds.
Returns a seq of the docs where the foreign keys have been updated to be the foreign documents, in the same order.
"
  [db foreign-coll foreign-path foreign-fields docs]
  (let [foreign-path (if (sequential? foreign-path) foreign-path [foreign-path]) ;; convert path to a seq if it's not one
        foreign-keys (->> docs (map #(get-in % foreign-path)) (filter some?) set) ;; the set of foreign keys to pre-fetch
        foreign-docs (->> (mc/find-maps db foreign-coll {:_id {mop/$in foreign-keys}} foreign-fields) ;; fetch the foreign keys and keep a map foreign-key -> foreign-doc
                          (reduce (fn [m {:keys [_id] :as fd}] (assoc m _id fd)) {}))]
    (->> docs (map #(update-in % foreign-path foreign-docs))) ;; replace foreign key with foreign doc in each document
    ))
