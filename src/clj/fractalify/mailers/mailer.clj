(ns fractalify.mailers.mailer
  (:require
    [com.stuartsierra.component :as c]
    [schema.core :as s]
    [clojure.java.io :as io]
    [fractalify.utils :as u]
    [selmer.parser :as sel]
    [plumbing.core :as p]
    [fractalify.mailers.protocols :as mp]
    [fractalify.users.schemas :as uch]))

(def tpl-dir "mail-templates/")

(def tpl-files
  (u/map-values (partial str tpl-dir)
                {:forgot-password "forgot-password.html"
                 :join            "join.html"}))

(s/defschema MailerConfig
  {:default-from s/Str
   :site         {s/Keyword s/Any}})

(defrecord Mailer [default-from site]
  c/Lifecycle
  (start [this]
    (->> tpl-files
         (u/map-values io/resource)
         (u/map-values slurp)
         (assoc this :templates)))

  (stop [this]
    (dissoc this :templates)))

(defn new-mailer [config site]
  (->> config
       (merge {:site site})
       (s/validate MailerConfig)
       map->Mailer))

(s/defschema EmailMessage
  {:to       uch/Email
   :from     s/Str
   :subject  s/Str
   :html     s/Str
   s/Keyword s/Str})

(s/defn send-email!
  [mailer
   template :- s/Keyword
   template-vals :- {s/Keyword s/Any}
   email :- {s/Keyword s/Str}]
  (p/letk [[templates :- {s/Keyword s/Str}
            default-from site mail-sender] mailer
           body (-> templates
                    template
                    (sel/render (merge site template-vals)))]
    (s/validate (s/protocol mp/MailSender) mail-sender)
    (->> email
         (merge {:from default-from :html body})
         (s/validate EmailMessage)
         (mp/send-email! mail-sender))))

