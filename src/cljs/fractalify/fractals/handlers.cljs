(ns fractalify.fractals.handlers
  (:require-macros [fractalify.tracer-macros :refer [trace-handlers]])
  (:require [fractalify.middleware :as m]
            [re-frame.core :as f]
            [fractalify.fractals.lib.l-systems :as l]
            [fractalify.fractals.lib.renderer :as renderer]
            [cljs.core.async :refer [<!]]
            [fractalify.utils :as u]
            [workers.core :as w]
            [fractalify.tracer]
            [instar.core :as i]
            [schema.core :as s :include-macros true]
            [fractalify.fractals.schemas :as ch]
            [fractalify.router :as t]
            [fractalify.db-utils :as d]
            [fractalify.components.dialog :as dialog]))


(def turtle-worker (new js/Worker "/js/turtle-worker.js"))


(f/register-handler
  :l-system-change
  m/standard-middlewares
  (fn [db [l-system]]
    (let [db (assoc-in db [:fractals :l-system-generating] true)]
      (f/dispatch [:generate-cmds l-system])
      db)))

(f/register-handler
  :generate-cmds
  m/standard-middlewares
  (fn [db [l-system]]
    (let [result-cmds (l/l-system l-system)]
      (w/on-message-once #(f/dispatch [:lines-generated %]) turtle-worker)
      (w/post-message [l-system result-cmds] turtle-worker))
    db))

(f/register-handler
  :lines-generated
  m/standard-no-debug
  (fn [db [lines]]
    (-> db
        (assoc-in [:fractals :forms :canvas :lines] lines)
        (assoc-in [:fractals :l-system-generating] false))))

(f/register-handler
  :canvas-change
  m/standard-no-debug
  (fn [db [canvas-dom canvas]]
    (renderer/render! canvas-dom canvas)
    db))

(f/register-handler
  :dissoc-l-system-operation
  m/standard-middlewares
  (s/fn [db path :- [(s/one ch/operation-type "oper-type") s/Int]]
    (f/dispatch (u/concat-vec [:dissoc-form-item :fractals :l-system] path))
    db))

(f/register-handler
  :assoc-l-system-operation
  m/standard-middlewares
  (s/fn [db [type] :- [(s/one ch/operation-type "oper-type")]]
    (let [last-id (-> (get-in db [:fractals :forms :l-system type]) keys sort last)
          val (condp = type
                :cmds ["" :default]
                :rules ["" ""])]
      (f/dispatch [:form-item :fractals :l-system type (inc last-id) val]))
    db))

(f/register-handler
  :fractal-publish
  m/standard-middlewares
  (fn [db _]
    (let [src (renderer/get-data-url)
          id (rand-int 9999)
          new-db (i/transform db [:fractals (i/%% :forms) :fractal-detail]
                              (fn [forms]
                                (assoc forms :id id
                                             :src src
                                             :author (d/logged-user db)
                                             :star-count 0
                                             :starred-by-me false)))]
      (dialog/hide-dialog!)
      (t/go! :fractal-detail :id id)
      new-db)))

(def starred-by-me? (u/partial-right get-in [:fractals :fractal-detail :starred-by-me]))

(f/register-handler
  :fractal-toggle-star
  m/standard-middlewares
  (fn [db _]
    (if-not (d/logged-user db)
      (do
        (f/dispatch [:show-snackbar "You must be logged in to star a fractal"])
        (t/go! :login)
        db)
      (let [path [:fractals :fractal-detail]
            f (if (starred-by-me? db) dec inc)]
        (-> db
            (update-in (into path [:star-count]) f)
            (update-in (into path [:starred-by-me]) not))))))






