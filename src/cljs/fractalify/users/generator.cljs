(ns fractalify.users.generator
  (:require [fractalify.generators :as g]
            [fractalify.utils :as u]
            [clojure.test.check.generators :as gen]
            [cljs-time.core :as m]
            [schema.core :as s :include-macros true]))


(defn gen-user []
  {:id       "122"
   :username "madvas"
   ;(gen/generate gen/string-alphanumeric 8)
   :bio      (g/gen-sentence 10 3 10)
   :gravatar "http://www.gravatar.com/avatar/bfdb252fe9d0ab9759f41e3c26d7700e.jpg"})

(defmethod g/generator :user
  [_ query-params]
  (gen-user))