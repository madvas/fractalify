(ns fractalify.img-cloud.img-cloud
  (:require [fractalify.img-cloud.protocols :as icp]
            [schema.core :as s]))

(s/defn upload
  [img-cloud :- (s/protocol icp/ImgCloud)
   filename :- s/Str
   src :- s/Str]
  (icp/upload img-cloud filename src))

(s/defn delete
  [img-cloud :- (s/protocol icp/ImgCloud)
   filename :- s/Str]
  (icp/delete img-cloud filename))

(s/defn thumb-url
  [img-cloud :- (s/protocol icp/ImgCloud)
   filename :- s/Str
   width :- s/Int
   height :- s/Int]
  (icp/thumb-url img-cloud filename width height))