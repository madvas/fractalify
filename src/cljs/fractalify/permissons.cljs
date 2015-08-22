(ns fractalify.permissons
  (:require [re-frame.core :as f]
            [fractalify.router :as t]
            [schema.core :as s :include-macros true]
            [fractalify.utils :as u]))

(defmulti permission identity)

(defmethod permission :login-required []
  (let [user (f/subscribe [:user])]
    (when-not @user
      [:login "You must be logged in to see this page"])))

(s/defn allowed-permissions
  [perms :- [s/Keyword]]
  (u/do-until-value (map #(partial permission %) perms)))

(s/defn redirect-disallowed! :- s/Bool
  [perms :- [s/Keyword]]
  (when-let [[redirect message] (allowed-permissions perms)]
    (println redirect message)
    (f/dispatch [:show-snackbar {:message message}])
    ;(t/go! redirect)
    true))

(defn login-required []
  (let [user (f/subscribe [:user])]
    (if-not user
      (do
        (f/dispatch [:show-snackbar {:message "You must be logged in to see this page"}])
        false)
      true)))


(def permissions
  {:login-required login-required})