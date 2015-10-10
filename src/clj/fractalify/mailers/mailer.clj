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

(def templates-dir "mail-templates")

(s/defschema MailerConfig
  {:default-from s/Str
   :site         {s/Keyword s/Any}})

(defrecord Mailer [default-from site]
  c/Lifecycle
  (start [this]
    this
    #_ (let [files (-> templates-dir io/resource io/file file-seq)
          templates
          (into {} (for [f files
                         :when (not (.isDirectory f))]
                     {(keyword (u/without-ext (.getName f)))
                      (slurp f)}))]
      (assoc this :templates templates)))

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
  #_ (p/letk [[templates default-from site mail-sender] mailer
           body (-> templates
                    template
                    (sel/render (merge site template-vals)))]
    (s/validate (s/protocol mp/MailSender) mail-sender)
    (->> email
         (merge {:from default-from :html body})
         (s/validate EmailMessage)
         (mp/send-email! mail-sender))))

