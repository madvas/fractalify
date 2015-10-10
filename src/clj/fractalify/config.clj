(ns fractalify.config
  (:require [fractalify.utils :as u]))

(defn config []
  {:site                 {:domain   "fractalify.herokuapp.com"
                          :protocol "http://"}
   :db-server            {:uri     (System/getenv "MONGOLAB_URI")
                          :host    "127.0.0.1"
                          :db-name "fractalify-dev"
                          :port    27017}
   :http-listener        {:port (System/getenv "PORT") 10555}
   :figwheel             {}
   :mailer               {:default-from "fractalify.herokuapp.com"}
   :sendgrid-mail-sender {:auth {:api-user (System/getenv "MAILER_USER")
                                 :api-key  (System/getenv "MAILER_PASS")}}
   :cloudinary           {:url (System/getenv "CLOUDINARY_URL")}})
