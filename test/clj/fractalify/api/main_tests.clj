(ns fractalify.api.main-tests
  (:require
    [fractalify.api :as a]
    [fractalify.api.users.users-generator :as ug]
    [fractalify.utils :as u]
    [plumbing.core :as p]
    [bidi.bidi :as b]
    [schema.core :as s]
    [fractalify.main.api-routes :as mar])
  (:use midje.sweet))

(def path-for (partial b/path-for (mar/get-routes)))

(def some-contact-form
  {:email   "some@email.com"
   :text    "Hello there"
   :subject "Some subject"})

(defn send-contact [contact-form]
  (a/post (path-for :contact) contact-form))

(a/init-test-system)
(with-state-changes
  [(before :facts (a/start-system))
   (after :facts (a/stop-system))]

  (fact "it sends contact form"
        (send-contact some-contact-form) => a/created)

  (fact "it doesnt send invalid contact form"
        (send-contact (dissoc some-contact-form :text)) => a/bad-request)

  )







