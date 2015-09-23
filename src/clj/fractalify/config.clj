(ns fractalify.config
  (:require [clojure.java.io :as io]
            [clojure.tools.reader.reader-types :as rt]
            [clojure.tools.reader :as r]))

(defn ^:private read-file
  [f]
  (r/read
    ;; This indexing-push-back-reader gives better information if the
    ;; file is misconfigured.
    (rt/indexing-push-back-reader
      (java.io.PushbackReader. (io/reader f)))))

(defn ^:private config-from
  [f]
  (if (.exists f)
    (read-file f)
    {}))

(defn ^:private user-config
  []
  (config-from (io/file (System/getProperty "user.home") ".fractalify.edn")))

(defn ^:private config-from-classpath
  []
  (if-let [res (io/resource "fractalify.edn")]
    (config-from (io/file res))
    {}))

(defn config
  "Return a map of the static configuration used in the component
  constructors."
  []
  (merge (config-from-classpath)
         (user-config)))
