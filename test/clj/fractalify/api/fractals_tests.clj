(ns fractalify.api.fractals-tests
  (:require
    [fractalify.api :as a]
    [fractalify.fractals.schemas :as fch]
    [fractalify.utils :as u]
    [plumbing.core :as p]
    [bidi.bidi :as b]
    [fractalify.api.users-tests :as ut]
    [fractalify.api.users.users-generator :as ug]
    [fractalify.api.fractals.fractals-generator :as fg]
    [fractalify.fractals.api-routes :as far])
  (:use midje.sweet))

(def some-fractal (-> (fg/gen-fractal)
                      (dissoc :src)
                      (assoc :data-url u/base64-png-prefix)))

(def some-comment
  {:text "some comment"})

(def path-for (partial b/path-for (far/get-routes)))

(defn path-for-fractal [route fractal]
  (path-for route :id (:id fractal)))

(defn path-for-comment [route fractal comment]
  (path-for route :id (:id fractal) :comment-id (:id comment)))

(defn get-fractals
  ([] (get-fractals {}))
  ([query]
   (a/get (path-for :fractals) {:query-params query})))

(defn fractal-list?
  ([items-count] (fractal-list? items-count fg/total-items-generated))
  ([items-count items-total]
   (every-checker a/status-ok
                  (a/response-schema fch/PublishedFractalsList)
                  (a/list-resp-items-total? items-total)
                  (a/list-resp-items-count? items-count))))

(def fractal?
  (every-checker a/status-ok
                 (a/response-schema fch/PublishedFractal)))

(defn new-fractal? [sent-fractal]
  (every-checker a/created
                 (a/response-schema fch/PublishedFractal)
                 (a/resp-has-map? (select-keys sent-fractal [:title :desc]))))

(def comments?
  (every-checker a/status-ok
                 (a/response-schema fch/CommentList)))

(defn new-comment? [comment]
  (every-checker a/created
                 (a/response-schema fch/Comment)
                 (a/resp-has-map? comment)))

(defn get-fractal-resp [fractal]
  (a/get (path-for-fractal :fractal fractal)))

(def get-fractal (comp :body get-fractal-resp))

(defn get-some-fractal-resp []
  (p/letk [[[:body items]] (get-fractals)]
    (get-fractal-resp (rand-nth items))))

(def get-some-fractal (comp :body get-some-fractal-resp))

(defn add-fractal [fractal]
  (a/put (path-for-fractal :fractals fractal) fractal))

(defn delete-fractal [fractal]
  (a/delete (path-for-fractal :fractal fractal)))

(defn get-fractal-comments [fractal]
  (a/get (path-for-fractal :fractal-comments fractal)))

(defn add-new-comment [fractal comment]
  (a/post (path-for-comment :fractal-comments fractal {}) comment))

(defn add-new-comment-somewhere [comment]
  (add-new-comment (get-some-fractal) comment))

(defn remove-comment [fractal comment]
  (a/delete (path-for-comment :fractal-comment fractal comment)))

(defn login-and-add-comment []
  (ut/login ut/some-user-login)
  (let [fractal (get-some-fractal)
        comment (:body (add-new-comment fractal some-comment))]
    [fractal comment]))

(defn login-and-get-some-fractal [login]
  (ut/login login)
  (get-some-fractal))

(defn login-and-add-fractal [login fractal]
  (ut/login login)
  (add-fractal fractal))

(def login-and-put-fractal-get (comp :body login-and-add-fractal))

(defn add-star [fractal]
  (a/post (path-for-fractal :fractal-star fractal) {}))

(defn remove-star [fractal]
  (a/delete (path-for-fractal :fractal-star fractal)))

(defn stars? [starred-by-me? star-count-expected]
  (p/fnk [starred-by-me star-count]
    (= starred-by-me? starred-by-me)
    (= star-count star-count-expected)))

(a/init-test-system)
(with-state-changes
  [(before :facts (a/start-system))
   (after :facts (a/stop-system))]

  (fact "gets list of fractals"
        (get-fractals {:limit 10}) => (fractal-list? 10)
        (get-fractals {:limit 5}) => (fractal-list? 5))

  (fact "gets list of fractals paginated"
        (let [first-fracs (get-fractals {:limit 2 :page 1 :sort (name :created)})
              second-fracs (get-fractals {:limit 1 :page 2 :sort (name :created)})]
          first-fracs => (fractal-list? 2)
          second-fracs => (fractal-list? 1)
          (= (-> (a/get-list-items first-fracs) second)
             (-> (a/get-list-items second-fracs) first))
          => true))

  (fact "new user has 0 fractals created"
        (ut/put-new-user ut/new-user)
        (get-fractals (u/select-key ut/new-user :username)) => (fractal-list? 0 0))

  (fact "gets one fractal by id"
        (get-some-fractal-resp) => fractal?)

  (fact "puts new fractal"
        (login-and-add-fractal ut/some-user-login some-fractal)
        => (new-fractal? some-fractal))

  (fact "new user has 1 fractal after adding 1 fractal"
        (ut/put-new-user ut/new-user)
        (login-and-add-fractal ut/new-user some-fractal)
        (get-fractals (u/select-key ut/new-user :username)) => (fractal-list? 1 1))

  (fact "dissallows to put invalid fractal"
        (login-and-add-fractal ut/some-user-login
                               (dissoc some-fractal :data-url))
        => a/bad-request)

  (fact "disallows unauthenticated to put new fractal"
        (add-fractal some-fractal) => a/unauthorized)

  (fact "allows user to delete his fractal"
        (let [fractal (login-and-put-fractal-get ut/some-user-login some-fractal)]
          (delete-fractal fractal) => a/no-content))

  (fact "disallows unauthenticated or unauthorized to delete a fractal"
        (let [fractal (login-and-put-fractal-get ut/some-user-login some-fractal)]
          (ut/logout)
          (delete-fractal fractal) => a/unauthorized
          (ut/login ut/some-other-user-login)
          (delete-fractal fractal) => a/unauthorized))

  (fact "allows admin to delete any fractal"
        (let [fractal (login-and-put-fractal-get ut/some-user-login some-fractal)]
          (ut/login ut/admin-login)
          (delete-fractal fractal) => a/no-content))

  (fact "gets list of fractal comments"
        (get-fractal-comments (get-some-fractal))
        => comments?)

  (fact "puts new comment to fractal"
        (ut/login ut/some-user-login)
        (let [fractal (get-some-fractal)]
          (add-new-comment fractal some-comment) => (new-comment? some-comment)
          (get-fractal-comments fractal) => (a/list-resp-has-map? some-comment)))

  (fact "doesn't allow to put comment to unauthenticated"
        (add-new-comment-somewhere some-comment) => a/unauthorized)

  (fact "returns not found for adding comment to non existent fractal"
        (ut/login ut/some-user-login) => (ut/user-response? ug/some-user)
        (add-new-comment {:id "non-exist"} some-comment)
        => a/not-found)

  (fact "allows user to delete his comment"
        (let [[fractal comment] (login-and-add-comment)]
          (remove-comment fractal comment) => a/no-content
          (get-fractal-comments fractal) => (a/list-resp-items-count? 0)))

  (fact "dissalows unauthenticated or unauthorized to delete a comment"
        (let [[fractal comment] (login-and-add-comment)]
          (ut/logout)
          (remove-comment fractal comment) => a/unauthorized
          (ut/login ut/some-other-user-login)
          (remove-comment fractal comment) => a/unauthorized
          (get-fractal-comments fractal) => (a/list-resp-items-count? 1)))

  (fact "allows admin to delete any comment"
        (let [[fractal comment] (login-and-add-comment)]
          (ut/login ut/admin-login)
          (remove-comment fractal comment) => a/no-content
          (get-fractal-comments fractal) => (a/list-resp-items-count? 0)))

  (fact "allows user to star fractal"
        (let [fractal (login-and-get-some-fractal ut/some-user-login)]
          (add-star fractal) => a/created
          (get-fractal fractal) => (stars? true 1)))

  (fact "allows user to unstar fractal"
        (let [fractal (login-and-get-some-fractal ut/some-user-login)]
          (add-star fractal) => a/created
          (remove-star fractal) => a/no-content
          (get-fractal fractal) => (stars? false 0)))

  (fact "disallows unauthenticated to star fractal"
        (add-star (get-some-fractal)) => a/unauthorized)

  (fact "disallows unauthenticated to unstar fractal"
        (remove-star (get-some-fractal)) => a/unauthorized)

  (fact "disallows user to star fractal multiple times"
        (let [fractal (login-and-get-some-fractal ut/some-user-login)]
          (add-star fractal)
          (add-star fractal)
          (get-fractal fractal) => (stars? true 1)))

  (fact "disallows user to unstar fractal multiple times"
        (let [fractal (login-and-get-some-fractal ut/some-user-login)]
          (add-star fractal)
          (remove-star fractal)
          (remove-star fractal)
          (get-fractal fractal) => (stars? false 0))))







