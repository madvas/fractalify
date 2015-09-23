(defproject fractalify "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url  "http://www.eclipse.org/legal/epl-v10.html"}

  :source-paths ["src/clj" "src/cljc"]

  :test-paths ["test/clj"]

  :dependencies [[org.clojure/clojure "1.7.0"]
                 [org.clojure/clojurescript "1.7.48" :scope "provided"]
                 [ring "1.4.0"]
                 [ring/ring-defaults "0.1.5"]
                 [compojure "1.4.0"]
                 [enlive "1.1.6"]
                 [environ "1.0.0"]
                 [http-kit "2.1.19"]
                 [fogus/ring-edn "0.3.0"]
                 [com.cemerick/drawbridge "0.0.6"]
                 [ring-basic-authentication "1.0.5"]
                 [reagent "0.5.1" :exclusions [cljsjs/react]]
                 ;[reagent "0.5.1"]
                 [re-frame "0.4.1"]
                 [cljs-ajax "0.3.14"]
                 [day8/re-frame-tracer "0.1.0-SNAPSHOT"]
                 [org.clojars.stumitchell/clairvoyant "0.1.0-SNAPSHOT"]
                 [binaryage/devtools "0.3.0"]
                 [prismatic/schema "1.0.0"]
                 [kibu/pushy "0.3.2"]
                 [rm-hull/monet "0.2.1"]
                 [bidi "1.21.0"]
                 [prismatic/plumbing "0.4.4"]
                 [instar "1.0.10"]
                 [camel-snake-kebab "0.3.2"]
                 [org.clojure/test.check "0.8.1"]
                 [com.andrewmcveigh/cljs-time "0.3.13"]
                 [clj-time "0.11.0"]
                 [com.stuartsierra/component "0.2.3"]
                 [liberator "0.13"]
                 [org.clojure/tools.logging "0.3.1"]
                 [juxt.modular/bidi "0.9.4"]
                 [juxt.modular/http-kit "0.5.4"]
                 [juxt.modular/maker "0.5.0"]
                 [juxt.modular/wire-up "0.5.0"]
                 [me.raynes/conch "0.8.0"]
                 [com.novemberain/monger "3.0.0-rc2"]
                 [clojurewerkz/scrypt "1.2.0"]
                 [cheshire "5.5.0"]]

  :plugins [[lein-cljsbuild "1.1.0"]
            [lein-environ "1.0.0"]
            [lein-less "1.7.3"]]

  :min-lein-version "2.5.0"

  :uberjar-name "fractalify.jar"

  :cljsbuild {:builds {:app           {:source-paths ["src/cljs" "src/cljc"]
                                       :compiler     {:main          fractalify.core
                                                      ;:preamble      ["resources/public/vendor/material-ui/material.js"]
                                                      :output-to     "resources/public/js/app.js"
                                                      :output-dir    "resources/public/js/out"
                                                      :source-map    "resources/public/js/out.js.map"
                                                      :optimizations :none
                                                      ;:optimizations :advanced
                                                      :externs       ["src/externs.js"]
                                                      :pretty-print  true}}
                       :turtle-worker {:source-paths ["src/cljs/workers" "src/cljc/fractalify/workers"]
                                       :compiler     {:main          fractalify.workers.turtle
                                                      :output-to     "resources/public/js/turtle-worker.js"
                                                      :output-dir    "resources/public/js/turtle-worker"
                                                      :optimizations :simple
                                                      :pretty-print  true
                                                      :externs       ["src/externs.js"]}}}}


  :less {:source-paths ["src/less"]
         :target-path  "resources/public/css"}

  :profiles {:dev     {:source-paths ["env/dev/clj"]
                       :test-paths   ["test/clj"]

                       :dependencies [[figwheel "0.3.7"]
                                      [figwheel-sidecar "0.3.7"]
                                      [com.cemerick/piggieback "0.1.5"]
                                      [weasel "0.6.0"]
                                      [io.aviso/pretty "0.1.18"]]

                       :repl-options {:init-ns          fractalify.user
                                      :welcome          (println "Type (dev) to start")
                                      :nrepl-middleware [cemerick.piggieback/wrap-cljs-repl
                                                         io.aviso.nrepl/pretty-middleware]}

                       :plugins      [[lein-figwheel "0.3.7"]]

                       :figwheel     {:http-server-root "public"
                                      :server-port      3449
                                      :css-dirs         ["resources/public/css"]
                                      :ring-handler     fractalify.server/http-handler
                                      :on-jsload        "fractalify.core/mount-root"}

                       :env          {:is-dev? true}

                       :cljsbuild    {:test-commands {"test" ["phantomjs" "env/test/js/unit-test.js" "env/test/unit-test.html"]}
                                      :builds        {:app  {:source-paths ["env/dev/cljs"]}
                                                      :test {:source-paths ["src/cljs" "test/cljs"]
                                                             :compiler     {:output-to     "resources/public/js/app_test.js"
                                                                            :output-dir    "resources/public/js/test"
                                                                            :source-map    "resources/public/js/test.js.map"
                                                                            :optimizations :whitespace
                                                                            :pretty-print  false}}}}}

             :uberjar {:source-paths ["env/prod/clj"]
                       :hooks        [leiningen.cljsbuild leiningen.less]
                       :env          {:production true}
                       :omit-source  true
                       :aot          :all
                       :main         fractalify.system
                       :cljsbuild    {:builds {:app
                                               {:source-paths ["env/prod/cljs"]
                                                :compiler
                                                              {:main            fractalify.core
                                                               :optimizations   :advanced
                                                               :closure-defines {:goog.DEBUG false}
                                                               :pretty-print    false}}
                                               :turtle-worker
                                               {:compiler {:optimizations   :advanced
                                                           :closure-defines {:goog.DEBUG false}
                                                           :pretty-print    false}}}}}})
