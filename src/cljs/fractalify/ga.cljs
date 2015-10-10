(ns fractalify.ga
  (:require [fractalify.router :as t]
            [fractalify.utils :as u]
            [schema.core :as s :include-macros true]))

(defn send-page-view [route route-params]
  (let [url (apply t/url route (flatten (vec route-params)))]
    (js/ga "send" "pageview" url)))


(s/defn send-event
  ([c a] (send-event c a nil))
  ([c a l] (send-event c a l nil))
  ([category :- (s/cond-pre s/Str s/Keyword)
    action :- (s/cond-pre s/Str s/Keyword)
    label :- (s/maybe s/Str)
    value :- (s/maybe s/Num)]
    (js/ga "send"
           (clj->js
             (merge {:hitType       "event"
                     :eventCategory category
                     :eventAction   action}
                    (when label {:eventLabel label})
                    (when value {:eventValue value})))))
  )