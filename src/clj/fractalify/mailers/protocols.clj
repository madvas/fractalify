(ns fractalify.mailers.protocols)

(defprotocol MailSender
  (send-email! [_ email]))