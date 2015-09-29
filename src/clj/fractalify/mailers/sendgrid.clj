(ns fractalify.mailers.sendgrid
  (:require
    [schema.core :as s]
    [fractalify.utils :as u]
    [plumbing.core :as p]
    [sendgrid-clj.core :as sg]
    [camel-snake-kebab.core :as csk]
    [fractalify.mailers.protocols :as mp]))

(s/defschema SendgridConfig
  {:auth {:api-user s/Str
          :api-key  s/Str}})

(defrecord SendgridMailSender [auth]
  mp/MailSender
  (send-email!
    [this email]
    (p/letk [[auth] this]
      (sg/send-email (u/map-keys csk/->snake_case auth) email)
      true)))

(defn new-sendgrid-mail-sender [config]
  (->> config
       (s/validate SendgridConfig)
       map->SendgridMailSender))
