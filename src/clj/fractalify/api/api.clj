(ns fractalify.api.api
  (:require [schema.core :as s]
            [cemerick.friend :as frd]
            [fractalify.utils :as u]
            [plumbing.core :as p]
            [clojure.set :as set]
            [monger.collection :as mc]
            [monger.operators :as mop]
            [io.clojure.liberator-transit :as lt]
            [liberator.representation :as lr]
            [cognitect.transit :as transit])
  (:import [org.bson.types ObjectId]
           (org.joda.time ReadableInstant)))

(defn malformed-params? [schema params]
  (fn [_] (s/check schema
                   (try
                     (u/coerce-str params schema)
                     (catch Exception e
                       #_(println "except" (u/coerce-str params schema))
                       params)))))

(def joda-time-writer
  (transit/write-handler
    (constantly "m")
    (fn [v] (-> ^ReadableInstant v .getMillis))
    (fn [v] (-> ^ReadableInstant v .getMillis .toString))))

(defn as-transit-response
  ([] (as-transit-response lr/as-response))
  ([f]
   (lt/as-response
     {:handlers            {org.joda.time.DateTime joda-time-writer}
      :allow-json-verbose? false}
     f)))


(def base-resource
  {:available-media-types ["application/transit+json"]
   :as-response           (as-transit-response)})

(def base-put
  (merge base-resource {:allowed-methods [:put]}))

(def base-delete
  (merge base-resource {:allowed-methods [:delete]}))

(def base-post
  (merge base-resource {:allowed-methods [:post]}))

(defn admin? [& _]
  (p/when-letk [[roles] (frd/current-authentication)]
    (contains? (set roles) :admin)))

(defn _id->id [x]
  (-> x
      (set/rename-keys {:_id :id})
      (update :id str)))

(s/defn id->_id
  [x :- (s/cond-pre {s/Keyword s/Any} (s/pred sequential?))]
  (cond-> x
          (map? x) (set/rename-keys {:id :_id})
          (sequential? x) (-> set (disj :id) (conj :_id) seq)))

(defn schema->fields [schema]
  (-> schema u/schema-keys id->_id))

(defn db->cljs [m schema]
  (when m
    (-> m
        _id->id
        (u/coerce-json schema))))

(s/defn insert-and-return
  ([db coll document schema]
    (insert-and-return db coll document schema (u/partial-right db->cljs schema)))
  ([db coll document schema transformation]
    (-> (mc/insert-and-return db coll (merge
                                        {:_id (ObjectId.)}
                                        document))
        _id->id
        (u/select-schema-keys schema)
        transformation)))

(defprotocol RouteResource
  (route->resource [route-resource]))

(defn populate
  "Populates the given docs sequence by looking up the 'foreign key' as an :_id in `foreign-coll`.
`foreign-path` can be either a single key or a sequence of keys (as in get-in)
Assumes the foreign keys are ObjectIds or coercable to objectIds.
Returns a seq of the docs where the foreign keys have been updated to be the foreign documents, in the same order.
"
  [db foreign-coll foreign-path foreign-key foreign-schema docs]
  (let [foreign-path (u/ensure-seq foreign-path)
        foreign-keys (->> (u/ensure-seq docs) (map #(get-in % foreign-path)) (filter some?) set)
        foreign-docs (->> (mc/find-maps db foreign-coll
                                        {foreign-key {mop/$in foreign-keys}}
                                        (schema->fields foreign-schema))
                          (reduce (fn [m fd]
                                    (assoc m (fd foreign-key)
                                             (db->cljs fd foreign-schema))) {}))]
    (let [res (->> (u/ensure-seq docs) (map #(update-in % foreign-path foreign-docs)))]
      (if (sequential? docs)
        res
        (first res)))))
