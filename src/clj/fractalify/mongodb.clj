(ns fractalify.mongodb
  (:require
    [monger.core :as m]
    [com.stuartsierra.component :as c]))

(defrecord MongoDb [uri host port db-name]
  c/Lifecycle
  (start [this]
    (merge this
      (if uri
        (m/connect-via-uri uri)
        (let [conn (m/connect {:host host :port port})]
          {:conn conn :db (m/get-db conn db-name)}))))

  (stop [this]
    (m/disconnect (:conn this))
    (dissoc this :conn :db)))

(def new-mongodb map->MongoDb)
