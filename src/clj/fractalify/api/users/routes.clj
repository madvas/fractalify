(ns fractalify.api.users.routes
  (:require
    [modular.ring :refer (WebRequestHandler)]
    [bidi.bidi :refer (path-for RouteProvider)]
    [liberator.core :refer [defresource]]
    [fractalify.utils :as u]
    [fractalify.api.api :as api]
    [bidi.bidi :as b]
    [cemerick.friend :as frd]
    [fractalify.api.users.users-db :as udb]
    [fractalify.api.main.routes :as mr]
    [schema.core :as s]
    [fractalify.users.schemas :as uch]
    [plumbing.core :as p]))

(def auth-base "/api/auth")
(def login-url (str auth-base "/login"))
(declare routes)

(defresource
  user [{:keys [db params]}]
  api/base-resource
  :handle-ok
  (fn [_]
    (let [fields (if (u/equal-in-key? :username
                                      (frd/current-authentication)
                                      params)
                   udb/private-fields
                   udb/public-fields)]
      (udb/get-user db (u/select-key params :username) fields))))

(defresource
  login [{:keys [db params]}]
  api/base-resource
  :allowed-methods [:post :get]
  :malformed? (api/malformed-params? uch/LoginForm params)
  :allowed?
  (fn [_]
    (frd/current-authentication))
  :post-redirect?
  (fn [_]
    {:location
     (b/path-for routes user :username (:username (frd/current-authentication)))}))

(defresource
  join [{:keys [db params]}]
  api/base-resource
  :allowed-methods [:put]
  :malformed? (api/malformed-params? uch/JoinForm params)
  :conflict?
  (fn [_]
    (udb/get-user-by-acc db (:username params) (:email params)))
  :put!
  (fn [_]
    {::user (apply dissoc (udb/add-user db (dissoc params :confirm-pass))
                   (keys udb/private-fields))})
  :handle-created ::user)

(defresource
  forgot-pass [{:keys [db params mailer]}]
  api/base-resource
  :allowed-methods [:post]
  :malformed? (api/malformed-params? uch/ForgotPassForm params)
  :can-post-to-missing? false
  :exists?
  (fn [_]
    (let [user (udb/get-user db {:email (:email params)} udb/public-fields)]
      [false {::user user}]
      false))
  :post!
  (fn [_]
    ))

(defn logout [_]
  (fn [res]
    (frd/logout* res)))

(def routes
  ["/api/" {"users" {["/" :username] user}
            "auth/" [["login" login]
                     ["logout" logout]
                     ["join" join]
                     ["forgot-pass" forgot-pass]]}])



(defrecord UserRoutes []
  RouteProvider
  (routes [_]
    routes))

(defn new-user-routes []
  (->UserRoutes))
