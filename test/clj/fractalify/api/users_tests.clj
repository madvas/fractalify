(ns fractalify.api.users-tests
  (:require
    [fractalify.api :as a]
    [fractalify.api.users.users-generator :as ug]
    [fractalify.users.schemas :as uch]
    [fractalify.utils :as u]
    [plumbing.core :as p]
    [bidi.bidi :as b]
    [fractalify.users.api-routes :as uar]
    [schema.core :as s])
  (:use midje.sweet))

(def admin-login (select-keys ug/admin [:username :password]))
(def some-user-login (select-keys ug/some-user [:username :password]))
(def some-other-user-login (select-keys ug/some-other-user [:username :password]))

(def new-user
  {:username     "someuser"
   :email        "some@gmail.com"
   :password     "111111"
   :confirm-pass "111111"
   :bio          ""})

(def new-user-created?
  (every-checker a/created
                 (a/response-schema uch/UserMe)
                 (a/resp-has-map? (u/select-key new-user :username))))

(def path-for (partial b/path-for (uar/get-routes)))

(defn put-new-user
  ([] (put-new-user new-user))
  ([user]
   (a/put (path-for :join) user)))

(defn login [user]
  (a/post (path-for :login) user))

(defn user-response?
  ([user] (user-response? user uch/UserMe))
  ([user schema]
   (every-checker (a/response-schema schema)
                  (a/resp-has-map? (u/select-key user :username)))))

(defn forgot-pass [user]
  (a/post (path-for :forgot-password)
          (u/select-key user :email)))

(def logged-user #(a/get (path-for :logged-user)))

(defn user-path-for [route user]
  (path-for route :username (:username user)))

(defn get-user [user]
  (a/get (user-path-for :user user)))

(def new-profile
  {:email "new-email@email.com"
   :bio   "new bio"})

(def logout (partial a/post (path-for :logout) {}))

(defn edit-profle [user]
  (a/post (user-path-for :edit-profile user) new-profile))

(defn change-pass [user]
  (a/post (user-path-for :change-password user)
          {:current-pass     (:password user)
           :new-pass         "somepass"
           :confirm-new-pass "somepass"}))

(a/init-test-system)
(with-state-changes
  [(before :facts (a/start-system))
   (after :facts (a/stop-system))]

  (fact "it doesn't login with bad credentials"
        (login {}) => a/forbidden
        (login {:username "baduser" :password "badpass"}) => a/forbidden)

  (fact "it logs in with good credentials"
        (login admin-login) => (user-response? ug/admin))

  (fact "it gets logged user"
        (login some-user-login) => (user-response? ug/some-user)
        ;(logged-user) => (user-response? ug/some-user)
        )

  (fact "it doesnt create invalid new user"
        (put-new-user (dissoc new-user :email)) => a/bad-request)

  (fact "it creates and logs in new user"
        (put-new-user) => new-user-created?
        ;(logged-user) => (user-response? new-user)
        )

  (fact "it disallows to create same user twice"
        (put-new-user) => new-user-created?
        (put-new-user) => a/conflict)

  (fact "it sends new pass in case of forgetting"
        (forgot-pass ug/some-user) => a/created)

  (fact "it returns not found when reseting unexisting account"
        (forgot-pass {:email "non-existent@email.com"}) => a/not-found)

  (fact "it loads some user"
        (get-user ug/some-user) => (user-response? ug/some-user uch/UserOther))

  (fact "it returns 404 for non existent user"
        (get-user {:username "non-existent"}) => a/not-found)

  (fact "it disallows to edit profile to unauthenticated"
        (edit-profle ug/some-user) => a/unauthorized)

  (fact "it edits profile to user"
        (login ug/some-user) => (user-response? ug/some-user)
        (edit-profle ug/some-user) => a/created
        (get-user ug/some-user) => (a/resp-has-map? new-profile))

  (fact "it allows admin to edit some user's profile"
        (login ug/admin) => (user-response? ug/admin)
        (edit-profle ug/some-user) => a/created
        (get-user ug/some-user) => (a/resp-has-map? new-profile))

  (fact "it disallows to change password to unauthenticated"
        (change-pass ug/some-user) => a/unauthorized)

  (fact "it changes password to user"
        (login ug/some-user) => (user-response? ug/some-user)
        (change-pass ug/some-user) => a/created)

  (fact "it allows admin to change some user's password"
        (login ug/admin) => (user-response? ug/admin)
        (change-pass ug/some-user) => a/created)

  (fact "it logs out user"
        (login ug/admin) => (user-response? ug/admin)
        (logout) => a/status-ok)
  )







