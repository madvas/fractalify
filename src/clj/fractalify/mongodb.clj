(ns fractalify.mongodb
  (:require
    [monger.core :as m]
    [monger.json]
    [monger.joda-time]
    [com.stuartsierra.component :as c]
    [schema.core :as s]
    [fractalify.utils :as u]))

(s/defschema MongoConfig
  {:uri                      (s/maybe s/Str)
   (s/optional-key :host)    s/Str
   (s/optional-key :port)    s/Int
   (s/optional-key :db-name) s/Str})

(defrecord MongoDb [uri host port db-name]
  c/Lifecycle
  (start [this]
    (merge this
           (if uri
             (m/connect-via-uri uri)
             (let [conn (m/connect {:host host :port port})]
               {:conn conn :db (m/get-db conn db-name)}))))

  (stop [this]
    (when-let [conn (:conn this)]
      (m/disconnect conn))
    (dissoc this :conn :db)))

(s/defn new-mongodb [config :- MongoConfig]
  (map->MongoDb config))
