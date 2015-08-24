(ns fractalify.permissons
  (:require [re-frame.core :as f]
            [fractalify.router :as t]
            [schema.core :as s :include-macros true]
            [fractalify.utils :as u]))

(defmulti permission identity)

(defmethod permission :login-required [_ db]
  (when-not (:user db)
    [:login "You must be logged in to see this page"]))

(s/defn validate-permissions :- (s/maybe {:redirect s/Keyword
                                          :message  s/Str})
  ([_] nil)
  ([db
    perms :- [s/Keyword]]
    (when-not (nil? perms)
      (when-let [error (u/validate-until-error db (map #(partial permission %) perms))]
        (zipmap [:redirect :message] error)))))