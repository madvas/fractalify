(ns fractalify.api.fractals.resources
  (:require
    [bidi.bidi :as b]
    [liberator.core :refer [defresource]]
    [fractalify.utils :as u]
    [fractalify.api.fractals.fractals-db :as fdb]
    [schema.core :as s]
    [fractalify.fractals.schemas :as fch]
    [plumbing.core :as p]
    [cemerick.friend :as frd]
    [fractalify.api.api :as a]
    [fractalify.fractals.api-routes :as far]))

(defn fractal-exists-fn [db params]
  (fn [& _]
    (when-let [fractal (fdb/fractal-get-by-id db (:id params))]
      {::fractal fractal})))

(p/defnk mine? [author]
  (u/eq-in-key? :username author (frd/current-authentication)))

(defresource
  fractal [{:keys [db params]}]
  a/base-resource
  :exists? (fractal-exists-fn db params)
  :handle-ok
  (s/fn :- fch/PublishedFractal [ctx]
    (::fractal ctx)))

(defresource
  fractal-put [{:keys [db params]}]
  a/base-put
  :malformed? (a/malformed-params? fch/PutFractalForm params)
  :authorized? (constantly (frd/current-authentication))
  :put!
  (fn [_]
    (let [fractal (-> params
                      (assoc :src "")
                      (dissoc :data-url))]
      {::fractal (fdb/fractal-insert-and-return db fractal (frd/current-authentication))}))
  :handle-created
  (s/fn :- fch/PublishedFractal [ctx]
    (::fractal ctx)))

(defresource
  fractal-delete [{:keys [db params]}]
  a/base-delete
  :authorized?
  (fn [_]
    (when-let [fractal (fdb/fractal-get-by-id db (:id params))]
      (let [allowed (or (a/admin?) (mine? fractal))]
        [allowed {::fractal fractal}])))
  :delete!
  (fn [ctx]
    (fdb/fractal-delete-by-id db (get-in ctx [::fractal :id]))))

(defresource
  fractals [{:keys [db params]}]
  a/base-resource
  :malformed? (a/malformed-params? fch/FractalListForm params)
  :handle-ok
  (s/fn :- fch/PublishedFractalsList [_]
    {:total-items (fdb/fractal-count db)
     :items       (fdb/get-fractals db params)}))

(defn star-fractal [db params unstar?]
  (fdb/star-fractal db (:id params) (frd/current-authentication) unstar?))

(defresource
  fractal-star [{:keys [db params]}]
  a/base-resource
  :allowed-methods [:delete :post]
  :authorized? (constantly (frd/current-authentication))
  :can-post-to-missing? false
  :exists? (fractal-exists-fn db params)
  :post!
  (fn [_]
    (star-fractal db params false))
  :delete!
  (fn [_]
    (star-fractal db params true)))

(defresource
  fractal-comments [{:keys [db params]}]
  a/base-resource
  :handle-ok
  (fn [_]
    (let [comments (fdb/get-comments db (:id params))]
      {:total-items (count comments)                        ; because currenlty without paging
       :items       comments})))

(defresource
  fractal-comment-post [{:keys [db params]}]
  a/base-post
  :malformed? (a/malformed-params? (merge fch/FractalIdField fch/CommentForm) params)
  :authorized? (constantly (frd/current-authentication))
  :can-post-to-missing? false
  :exists? (fractal-exists-fn db params)
  :post!
  (fn [_]
    (let [[fractal comment] (u/split-map params :id)]
      {::comment (fdb/comment-insert-and-return
                   db comment (:id fractal)
                   (frd/current-authentication))}))
  :handle-created
  (s/fn :- fch/Comment [ctx]
    (::comment ctx)))


(defresource
  fractal-comment-delete [{:keys [db params]}]
  a/base-delete
  :authorized?
  (fn [_]
    (let [comment (fdb/get-comment-by-id db (:comment-id params))]
      (or (a/admin?) (mine? comment))))
  :delete!
  (fn [_]
    (fdb/comment-delete-by-id db (:comment-id params))))

(def routes->resources
  {:fractal                fractal
   :fractal-add            fractal-put
   :fractal-remove         fractal-delete
   :fractals               fractals
   :fractal-comments       fractal-comments
   :fractal-comment-add    fractal-comment-post
   :fractal-comment-remove fractal-comment-delete
   :fractal-star           fractal-star})

(defrecord FractalRoutes []
  b/RouteProvider
  (routes [_]
    (far/get-routes true))

  a/RouteResource
  (route->resource [_]
    routes->resources))

(defn new-fractal-routes []
  (->FractalRoutes))
