(ns fractalify.mailer
  (:require
    [com.stuartsierra.component :as c]
    [sendgrid-clj.core :as g]
    [schema.core :as s]
    [clojure.java.io :as io]
    [fractalify.utils :as u]
    [selmer.parser :as sel]
    [plumbing.core :as p]))

(def templates-dir "mail-templates")

(def MailerAuthSchema
  {:api-user s/Str
   :api-key  s/Str})

(def MailerSchema
  {:auth      MailerAuthSchema
   :templates {s/Keyword s/Str}})

(defrecord Mailer [auth]
  c/Lifecycle
  (start [this]
    (let [files (-> templates-dir io/resource io/file file-seq)
          templates (for [f files
                          :when (not (.isDirectory f))]
                      {(.getName f) (slurp f)})]

      (assoc this :auth auth
                  :templates (u/p "tepls:" templates))))

  (stop [this]
    (dissoc this :auth :templates)))

(defn new-mailer [opts]
  (->>
    opts
    (s/validate MailerAuthSchema)
    ->Mailer))

(s/defn send-email!
  [mailer :- MailerSchema
   template :- s/Keyword
   template-vals :- {s/Keyword s/Any}
   opts :- {s/Keyword s/Str}]
  (p/letk [[templates auth] mailer
           body (-> templates template (sel/render template-vals))]
    (println body)))