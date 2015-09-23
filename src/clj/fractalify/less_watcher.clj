(ns fractalify.less-watcher
  (:require
    [com.stuartsierra.component :as c]
    [me.raynes.conch.low-level :as sh]))

(defrecord LessWatcher []
  c/Lifecycle
  (start [this]
    this
    #_ (assoc this :proc (sh/proc "lein" "less" "auto")))

  (stop [this]
    #_ (sh/destroy (:proc this))
    (dissoc this :proc)))

(def new-less-watcher ->LessWatcher)

