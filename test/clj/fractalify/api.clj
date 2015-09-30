(ns fractalify.api
  (:refer-clojure :exclude [get])
  (:require [fractalify.dev :as dev]
            [com.stuartsierra.component :as c]
            [fractalify.system :as sys]
            [fractalify.mailers.mock-mail-sender :as mms]
            [fractalify.config :as cfg]
            [clj-http.client :as client]
            [clj-http.core :as http]
            [plumbing.core :as p]
    #_[midje.checking.checkers.defining :as]
            [schema.core :as s]
            [fractalify.utils :as u]
            [clojure.edn :as edn]
            [fractalify.readers :as r]
            [fractalify.api.users.routes :as ur]
            [bidi.bidi :as b])
  (:use midje.sweet))

(def server-port 10556)
(def server-url (str "http://localhost:" server-port))
(def ^:dynamic *cookies* (clj-http.cookies/cookie-store))

(defn init-test-system []
  (let [deps (merge sys/db-generators-dependencies (sys/new-dependency-map))]
    (-> (sys/new-system-map (merge (cfg/config) {:http-listener {:port server-port}}))
        (assoc :mail-sender (mms/new-mock-mail-sender))
        (sys/generator-components nil)
        (c/system-using deps)
        (dev/init))))

(def stop-system dev/stop)
(def start-system dev/start)

(defn clear-cookies []
  (alter-var-root #'*cookies* (constantly (clj-http.cookies/cookie-store))))

(defn parse-edn-response [res]
  (p/update-in-when res [:body]
                    (partial edn/read-string {:readers (r/get-readers)
                                              :eof     nil})))

(defn request [path opts]
  (-> (client/request (merge {:url              (str server-url path)
                              ;         "http://requestb.in/1cxotfm1"
                              :headers          {"Content-type" "application/edn"
                                                 "Accept"       "application/edn"}
                              :force-redirects  true
                              :throw-exceptions false
                              :as               :text
                              :cookie-store     *cookies*}
                             (-> opts
                                 (p/update-in-when [:body] str))))
      parse-edn-response))

(defn get
  ([path] (get path {}))
  ([path opts]
   (request path (merge {:method :get} opts))))

(defn post
  ([path body] (post path body {}))
  ([path body opts]
   (request path (merge {:method :post
                         :body   body} opts))))

(defn put
  ([path body] (put path body {}))
  ([path body opts]
   (request path (merge {:method :put
                         :body   body} opts))))

(defn delete [path opts]
  (request path (merge {:method :delete} opts)))

(defn status-checker [expected-status]
  (p/fnk [status & _]
    (= status expected-status)))

(defn response-schema [schema]
  (p/fnk [body & _]
    (s/validate schema body)))

(defn resp-has-map? [m]
  (p/fnk resp-has-map [body & _]
    (u/submap? m body)))

(def bad-request (status-checker 400))
(def unauthorized (status-checker 401))
(def forbidden (status-checker 403))
(def not-found (status-checker 404))
(def created (status-checker 201))
(def status-ok (status-checker 200))
(def conflict (status-checker 409))