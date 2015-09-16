(ns fractalify.web-server
  (:require [clojure.java.io :as io]
            [fractalify.dev :refer [is-dev? inject-devmode-html browser-repl start-figwheel start-less]]
            [compojure.core :refer [GET POST defroutes]]
            [liberator.core :refer [resource defresource]]
            [compojure.route :refer [resources]]
            [net.cgrand.enlive-html :refer [deftemplate]]
            [net.cgrand.reload :refer [auto-reload]]
            [ring.middleware.reload :as reload]
            [ring.middleware.defaults :refer [wrap-defaults api-defaults]]
            [fractalify.utils :refer [generate-response]]
            [environ.core :refer [env]]
            [org.httpkit.server :refer [run-server]]
            [ring.middleware.edn :refer [wrap-edn-params]]
            [cemerick.drawbridge :as drawbridge]
            [ring.middleware.basic-authentication :refer [wrap-basic-authentication]]
            [ring.middleware.params :as params]
            [ring.middleware.keyword-params :as keyword-params]
            [ring.middleware.nested-params :as nested-params]
            [ring.middleware.session :as session]
            [plumbing.core :as p]
            [bidi.bidi :as b]
            [bidi.ring :as br]
            [com.stuartsierra.component :as component])
  (:gen-class))

(deftemplate page (io/resource "index.html") []
             [:body] (if is-dev? inject-devmode-html identity))

(defresource parameter [params]
             :available-media-types ["text/plain"]
             :exists? false
             :handle-ok (fn [_] (format "The text is %s" (:txt params))))

(def res (resource :available-media-types ["text/html"]
                   :handle-ok "<html>Hello, Internet.</html>"))


(def routes
  ["/" {["bar/" :txt] parameter
        ""            (br/->Resources nil)}])

(defn dispatch-route [resource]
  (fn [res]
    ((resource (:params res)) res)))

(defn authenticated? [name pass]
  (= [name pass] [(System/getenv "AUTH_USER") (System/getenv "AUTH_PASS")]))

(def drawbridge-handler
  (-> (drawbridge/ring-handler)
      (keyword-params/wrap-keyword-params)
      (nested-params/wrap-nested-params)
      (params/wrap-params)
      (session/wrap-session)))

(defn http-handler [handler]
  (-> handler
      #_(wrap-defaults api-defaults)
      wrap-edn-params))

(defn wrap-http [handler]
  (fn [req]
    (let [handler (if (= "/repl" (:uri req))
                    (wrap-basic-authentication drawbridge-handler authenticated?)
                    (-> handler
                        http-handler
                        (p/?> is-dev? reload/wrap-reload)))]
      (handler req))))

(defn run-web-server [& [port]]
  (let [port (Integer. (or port (env :port) 10555))]
    (println (format "Starting web server on port %d." port))
    (run-server (wrap-http
                  (br/make-handler routes dispatch-route)
                  ;(br/make-handler routes)
                  ) {:port port :join? false})
    ))

(defn run-auto-reload [& [port]]
  (auto-reload *ns*)
  (start-figwheel)
  (start-less))

(defn run [& [port]]
  (when is-dev?
    (run-auto-reload))
  (run-web-server port))

(defn stop-server []
  (when-not (nil? @server)
    (@server :timeout 0)
    (reset! server nil)))

(defn restart-server []
  (stop-server)
  (run-web-server))

(defn -main [& [port]]
  (run port))

(defrecord WebServer [port]
  component/Lifecycle

  (start [this]
    (let [port (Integer. (or port (env :port) 10555))]
      (println (format "Starting web server on port %d." port))
      (assoc this :web-server
                  (run-server
                    (wrap-http
                      (br/make-handler routes dispatch-route)
                      ;(br/make-handler routes)
                      ) {:port port :join? false}))))

  (stop [this]
    (println this)
    ((:web-server this) :timeout 0)
    this))

(defn http-server [config]
  (map->WebServer config))