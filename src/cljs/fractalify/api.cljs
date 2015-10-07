(ns fractalify.api
  (:require [fractalify.utils :as u]
            [schema.core :as s :include-macros true]
            [fractalify.main.schemas :as ch]
            [ajax.core :refer [GET POST PUT DELETE]]
            [fractalify.users.api-routes :as uar]
            [fractalify.main.api-routes :as mar]
            [fractalify.fractals.api-routes :as far]
            [bidi.bidi :as b]
            [plumbing.core :as p]
            [fractalify.main.schemas :as mch]))

(def all-routes
  ["" [(mar/get-routes) (uar/get-routes) (far/get-routes)]])

(s/defn path-for :- s/Str
  [route :- s/Keyword
   route-params :- {s/Keyword s/Any}]
  (apply b/path-for all-routes route (flatten (seq route-params))))

(def default-opts
  {:headers {:Accept ["application/transit+json"]}
   :format  :transit})

(s/defn fetch!
  [api-route :- s/Keyword
   query-params :- ch/QueryParams
   route-param-names :- [s/Keyword]
   opts]
  (let [url (path-for api-route (select-keys query-params route-param-names))]
    (GET url (merge default-opts
                    opts
                    {:params (-> query-params clj->js js->clj)}))))


(s/defn send! [opts :- mch/ApiSendOpts]
  (p/letk [[api-route {route-params {}}] opts
           url (path-for api-route route-params)]
    (let [f (condp = (:method opts)
              :put PUT
              :post POST
              :delete DELETE)]
      (f url (merge default-opts
                    opts
                    {:format          :transit
                     :response-format :transit
                     :headers         {:Accept ["application/transit+json"]}})))))
