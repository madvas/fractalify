(ns fractalify.api.users.resources
  (:require
    [modular.ring :refer (WebRequestHandler)]
    [bidi.bidi :refer (path-for RouteProvider)]
    [liberator.core :refer [defresource]]
    [fractalify.utils :as u]
    [fractalify.api.api :as a]
    [bidi.bidi :as b]
    [cemerick.friend :as frd]
    [fractalify.api.users.users-db :as udb]
    [schema.core :as s]
    [fractalify.users.schemas :as uch]
    [plumbing.core :as p]
    [fractalify.mailers.mailer :as mm]
    [fractalify.users.api-routes :as uar]
    [cemerick.friend.workflows :as workflows]
    [liberator.representation :as lr]))

(def auth-base "/api/auth")
(def login-url (str auth-base "/login"))

(defn auth-workflow
  [db uri]
  {:credential-fn (partial udb/verify-credentials db)
   :workflows     [(workflows/interactive-form :login-uri uri
                                               :redirect-on-auth? false)]})

(defn authenticate [db request]
  (let [request (assoc request :request-method :post)       ; bit hack, so authentication works with put when creating new acc
        ring-response ((frd/authenticate identity (auth-workflow db (:uri request))) request)]
    (when-not (= 302 (:status ring-response))
      ring-response)))

(defn me? [params]
  (fn [& _]
    (when-let [user (frd/current-authentication)]
      (u/eq-in-key? :username params user))))

(s/defn get-user [db params]
  (let [me (or ((me? params)) (a/admin?))
        schema (if me uch/UserMe uch/UserOther)]
    (udb/get-user db (u/select-key params :username) schema)))


(defresource
  logged-user [{:keys [db params]}]
  a/base-resource
  :handle-ok
  (fn [_]
    (when-let [user (frd/current-authentication)]
      (get-user db (u/select-key user :username)))))

(defresource
  user [{:keys [db params]}]
  a/base-resource
  :exists?
  (fn [_]
    (when-let [user (get-user db params)]
      {::user user}))
  :handle-ok ::user)

(defresource
  join [{:keys [db params]}]
  a/base-put
  :malformed? (a/malformed-params? uch/JoinForm params)
  :conflict?
  (fn [_]
    (udb/get-user-by-acc db (:username params) (:email params) uch/UserId))
  :put!
  (fn [_]
    {::user (udb/user-insert-and-return db (dissoc params :confirm-pass) uch/UserMe)})
  :handle-created
  (fn [ctx]
    (lr/ring-response
      (::user ctx)
      (let [ring-response (authenticate db (:request ctx))]
        (dissoc ring-response :body)))))

(defresource
  login [{:keys [db params]}]
  a/base-post
  :post!
  (p/fnk [request]
    (when-let [ring-response (authenticate db request)]
      {::ring-response ring-response}))
  :post-redirect? false
  :handle-created
  (fn [ctx]
    (apply
      lr/ring-response
      (if-let [ring-response (::ring-response ctx)]
        (let [user-session (frd/current-authentication ring-response)]
          [(udb/get-user db (u/select-key user-session :username) uch/UserMe)
           (dissoc ring-response :body)])
        [{:status 403}]))))

(defresource
  forgot-pass [{:keys [db params mailer]}]
  a/base-post
  :malformed? (a/malformed-params? uch/ForgotPassForm params)
  :can-post-to-missing? false
  :exists?
  (fn [_]
    (when-let [user (udb/get-user db {:email (:email params)} uch/UserMe)]
      {::user user}))
  :post!
  (fn [ctx]
    (p/letk [[id username email] (::user ctx)
             token (udb/create-reset-token db id)]
      (mm/send-email! mailer :forgot-password
                      {:token    token
                       :username username}
                      {:to      email
                       :subject "Forgotten password"}))))

(defn set-new-pass-fn [db params]
  (fn [_]
    (p/letk [[username new-pass] params]
      (udb/set-new-password db username new-pass))))

(defresource
  reset-pass [{:keys [db params]}]
  a/base-post
  :malformed?
  (a/malformed-params?
    (merge uch/ResetPassForm uch/UsernameField) params)
  :authorized?
  (u/or-fn
    a/admin?
    (fn valid-reset-token? [_]
      (p/letk [[username token] params]
        (udb/get-user-by-reset-token db username token))))
  :post! (set-new-pass-fn db params))

(defresource
  change-pass [{:keys [db params]}]
  a/base-post
  :malformed?
  (a/malformed-params?
    (merge uch/ChangePassForm uch/UsernameField) params)
  :authorized?
  (u/or-fn
    a/admin?
    (u/and-fn
      (me? params)
      (fn current-pass-matches? [_]
        (p/letk [[username current-pass] params]
          (udb/verify-credentials db {:username username
                                      :password current-pass})))))
  :post! (set-new-pass-fn db params))

(defresource
  edit-profile [{:keys [db params]}]
  a/base-post
  :malformed?
  (a/malformed-params?
    (merge uch/EditProfileForm uch/UsernameField) params)
  :authorized? (u/or-fn a/admin? (me? params))
  :post!
  (fn [_]
    (let [[username-entry other-entries] (u/split-map params :username)]
      (udb/update-user db username-entry other-entries))))

(defn logout [_]
  (fn [res]
    (frd/logout* res)))

(def routes->resources
  {:reset-password  reset-pass
   :change-password change-pass
   :edit-profile    edit-profile
   :user            user
   :logged-user     logged-user
   :login           login
   :logout          logout
   :join            join
   :forgot-password forgot-pass})

(defrecord UserRoutes []
  RouteProvider
  (routes [_]
    (uar/get-routes))

  a/RouteResource
  (route->resource [_]
    routes->resources))

(defn new-user-routes []
  (->UserRoutes))
