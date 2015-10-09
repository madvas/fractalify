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