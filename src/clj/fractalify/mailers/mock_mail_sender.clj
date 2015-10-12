(ns fractalify.mailers.mock-mail-sender
  (:require
    [fractalify.mailers.protocols :as mp]))

(defrecord MockMailSender []
  mp/MailSender
  (send-email [_ email]
    email))

(def new-mock-mail-sender ->MockMailSender)
