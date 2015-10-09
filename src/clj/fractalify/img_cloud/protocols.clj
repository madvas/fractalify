(ns fractalify.img-cloud.protocols)

(defprotocol ImgCloud
  (upload [_ filename src])
  (delete [_ filename]))