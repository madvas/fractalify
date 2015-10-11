(ns fractalify.img-cloud.mock-img-cloud
  (:require [fractalify.img-cloud.protocols :as icp]
            [com.stuartsierra.component :as c]
            [schema.core :as s]))

(defrecord MockImgCloud []
  c/Lifecycle
  (start [this]
    (assoc this :img-cloud {}))

  (stop [this]
    (dissoc this :img-cloud))

  icp/ImgCloud
  (upload [_ filename src]
    {"url" filename})

  (delete [_ filename]
    {"status" "ok"})

  (thumb-url [_ filename width height]
    (str "thumb_" filename)))

(def new-mock-img-cloud ->MockImgCloud)
