(ns fractalify.db-generator
  (:require
    [monger.core :as m]
    [com.stuartsierra.component :as c]))

(defprotocol Generator
  (generate [db conn]
    ))

(defrecord DbGenerator [generators]
  c/Lifecycle
  (start [this]
    )

  (stop [this]
    ))

(def new-db-generator map->DbGenerator)

