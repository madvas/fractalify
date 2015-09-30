(ns fractalify.api.users.routes
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
    [fractalify.mailers.mailer :as mm]))

(def auth-base "/api/auth")
(def login-url (str auth-base "/login"))
(declare routes)

(defn me? [params]
  (fn [& _]
    (when-let [user (frd/current-authentication)]
      (u/eq-in-key? :username params user))))

(defn get-user-fn [db params]
  (let [me (or ((me? params)) (a/admin?))]
    (s/fn get-user :- (s/maybe (s/conditional (constantly me)
                                              uch/UserMe
                                              :else uch/UserOther)) [_]
      (let [schema (if me uch/UserMe uch/UserOther)]
        (udb/get-user db (u/select-key params :username) schema)))))


(defresource
  logged-user [{:keys [db params]}]
  a/base-resource
  :exists? (fn [_] (frd/current-authentication))
  :handle-ok
  (get-user-fn db (u/select-key (frd/current-authentication) :username)))

(defresource
  user [{:keys [db params]}]
  a/base-resource
  :handle-ok (get-user-fn db params))

(defresource
  join [{:keys [db params]}]
  a/base-resource
  :allowed-methods [:put]
  :malformed? (a/malformed-params? uch/JoinForm params)
  :conflict?
  (fn [_]
    (udb/get-user-by-acc db (:username params) (:email params) uch/UserId))
  :put!
  (fn [_]
    {::user (udb/user-insert-and-return db (dissoc params :confirm-pass) uch/UserMe)})
  :handle-created
  (s/fn :- uch/UserMe [ctx]
    (::user ctx)))

(defresource
  login [{:keys [db params]}]
  a/base-resource
  :allowed-methods [:post :get]
  :allowed?
  (fn [ctx]
    (-> ctx :request :request-method)
    (frd/current-authentication))
  :post-redirect?
  (fn [_]
    {:location
     (b/path-for routes user :username (:username (frd/current-authentication)))}))

(defresource
  forgot-pass [{:keys [db params mailer]}]
  a/base-resource
  :allowed-methods [:post]
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
      (mm/send-email! mailer :forgot-pass
                      {:token    token
                       :username username}
                      {:to      email
                       :subject "Forgotten password"}))))

(defn set-new-pass [db params]
  (fn [_]
    (p/letk [[username new-pass] params]
      (udb/set-new-password db username new-pass))))

(defresource
  reset-pass [{:keys [db params]}]
  a/base-resource
  :allowed-methods [:post]
  :malformed?
  (a/malformed-params?
    (merge uch/ResetPassForm uch/UsernameField) params)
  :authorized?
  (u/or-fn
    a/admin?
    (fn valid-reset-token? [_]
      (p/letk [[username token] params]
        (udb/get-user-by-reset-token db username token))))
  :post! (set-new-pass db params))

(defresource
  change-pass [{:keys [db params]}]
  a/base-resource
  :allowed-methods [:post]
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
  :post! (set-new-pass db params))

(defresource
  edit-profile [{:keys [db params]}]
  a/base-resource
  :allowed-methods [:post]
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

(def routes
  ["/api/" {"users" {["/" :username] [["/reset-pass" reset-pass]
                                      ["/change-pass" change-pass]
                                      ["/edit-profile" edit-profile]
                                      ["" user]]}
            "auth/" [["logged-user" logged-user]
                     ["login" login]
                     ["logout" logout]
                     ["join" join]
                     ["forgot-pass" forgot-pass]]}])



(defrecord UserRoutes []
  RouteProvider
  (routes [_]
    routes))

(defn new-user-routes []
  (->UserRoutes))
