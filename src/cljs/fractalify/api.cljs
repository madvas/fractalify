(ns fractalify.api
  (:require [fractalify.utils :as u]
            [schema.core :as s :include-macros true]
            [fractalify.main.schemas :as ch]
            [ajax.core :as aj :refer [GET POST PUT DELETE]]
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

(s/defn fetch!
  [api-route :- s/Keyword
   query-params :- ch/QueryParams
   route-param-names :- [s/Keyword]
   handler
   error-handler]
  (let [url (path-for api-route (select-keys query-params route-param-names))]
    (GET (u/p "url:" url) {:params        (-> query-params clj->js js->clj)
                           :format        :transit
                           :headers       {:Accept ["application/transit+json"]}
                           :handler       handler
                           :error-handler error-handler})))


(s/defn send! [opts :- mch/ApiSendOpts]
  (p/letk [[api-route {route-params {}} handler error-handler] opts
           url (path-for api-route route-params)]
    (aj/ajax-request (merge opts
                            {:uri             url
                             :format          :transit
                             :response-format :transit
                             :headers         {:Accept ["application/transit+json"]}
                             :handler         (fn [[ok result]]
                                                (if ok
                                                  (handler result)
                                                  (error-handler result)))}))))
