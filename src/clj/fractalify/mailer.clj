(ns fractalify.mailer
  (:require
    [com.stuartsierra.component :as c]
    [sendgrid-clj.core :as g]
    [schema.core :as s]
    [clojure.java.io :as io]
    [fractalify.utils :as u]
    [selmer.parser :as sel]
    [plumbing.core :as p]
    [sendgrid-clj.core :as sg]
    [camel-snake-kebab.core :as csk]))

(def templates-dir "mail-templates")

(def MailerConfigSchema
  {:auth         {:api-user s/Str
                  :api-key  s/Str}
   :default-from s/Str
   :site         {s/Keyword s/Any}})

(def MailerSchema
  (merge MailerConfigSchema
         {:templates {s/Keyword s/Str}}))

(defrecord Mailer [auth default-from site]
  c/Lifecycle
  (start [this]
    (let [files (-> templates-dir io/resource io/file file-seq)
          templates
          (into {} (for [f files
                         :when (not (.isDirectory f))]
                     {(keyword (u/without-ext (.getName f)))
                      (slurp f)}))]
      (assoc this :templates templates)))

  (stop [this]
    (dissoc this :auth :templates)))

(defn new-mailer [opts site]
  (->> opts
       (merge {:site site})
       (s/validate MailerConfigSchema)
       map->Mailer))

(s/defn send-email!
  [mailer :- MailerSchema
   template :- s/Keyword
   template-vals :- {s/Keyword s/Any}
   opts :- {s/Keyword s/Str}]
  (p/letk [[templates auth default-from site] mailer
           body (-> templates template (sel/render (merge site template-vals)))
           auth (u/map-keys csk/->snake_case auth)]
    (sg/send-email auth (merge
                          {:from default-from
                           :html body}
                          opts))
    true))