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
            [schema.core :as s :include-macros true]
            [fractalify.fractals.schemas :as fch]
            [fractalify.router :as t]
            [fractalify.handler-utils :as d]
            [fractalify.components.dialog :as dialog]
            [plumbing.core :as p]
            [com.rpl.specter :as e]))


(def turtle-worker (new js/Worker "/public/js/turtle-worker.js"))

(def fractal-detail (u/partial-right get-in [:fractals :fractal-detail]))
(def starred-by-me? (u/partial-right get-in [:fractals :fractal-detail :starred-by-me]))

(s/defn assoc-fractal-detail [db fractal :- fch/PublishedFractal]
  (d/assoc-with-query-params db [:fractals :fractal-detail]
                             fractal (u/select-key fractal :id)))

(s/defn update-sidebar-fractal [db fractal :- fch/PublishedFractal]
  (e/transform [:fractals :fractals-sidebar :items e/ALL #(u/eq-in-key? :id % fractal)]
               (constantly fractal) db))

(trace-handlers
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
    (s/fn [db path :- [(s/one fch/operation-type "oper-type") s/Int]]
      (f/dispatch (u/concat-vec [:dissoc-form-item :fractals :l-system] path))
      db))

  (f/register-handler
    :assoc-l-system-operation
    m/standard-middlewares
    (s/fn [db [type] :- [(s/one fch/operation-type "oper-type")]]
      (let [last-id (-> (get-in db [:fractals :forms :l-system type]) keys sort last)
            val (condp = type
                  :cmds ["" :default]
                  :rules ["" ""])]
        (f/dispatch [:set-form-item :fractals :l-system type (inc last-id) val]))
      db))

  (f/register-handler
    :fractal-publish
    m/standard-middlewares
    (fn [db _]
      (let [data-url (renderer/get-data-url)
            info (d/get-form-data db :fractals :info)
            l-system (d/get-form-data db :fractals :l-system)
            canvas (-> (d/get-form-data db :fractals :canvas)
                       (dissoc :lines))]
        (f/dispatch [:api-put
                     {:api-route :fractals
                      :params    (merge
                                   info
                                   {:l-system l-system
                                    :canvas   canvas
                                    :data-url data-url})
                      :handler   :fractal-publish-res}])
        (dialog/hide-dialog!)
        db)))

  (f/register-handler
    :fractal-publish-res
    m/standard-middlewares
    (fn [db [fractal]]
      (t/go! :fractal-detail :id (:id fractal))
      (assoc-fractal-detail db fractal)))

  (f/register-handler
    :fractal-toggle-star
    [m/standard-middlewares (f/undoable "fractal-toggle-star")]
    (fn [db [id]]
      (if-not (d/logged-user db)
        (do (d/snack-n-go! "Please log in first in order to star a fractal" :login)
            db)
        (let [path [:fractals :fractal-detail]
              [f dispatch] (if (starred-by-me? db) [dec :api-delete]
                                                   [inc :api-post])]
          (f/dispatch [dispatch
                       {:api-route    :fractal-star
                        :route-params {:id id}
                        :error-undo?  true}])
          (-> db
              (update-in (into path [:star-count]) f)
              (update-in (into path [:starred-by-me]) not)
              (#(update-sidebar-fractal % (get-in % path))))))))

  (f/register-handler
    :fractal-comment-add
    [m/standard-middlewares]
    (fn [db [id]]
      (f/dispatch [:api-post
                   {:api-route    :fractal-comments
                    :params       (d/get-form-data db :fractals :comment)
                    :route-params {:id id}
                    :handler      :fractal-comment-add-resp}])
      (assoc-in db [:fractals :forms :comment :text] "")))

  (f/register-handler
    :fractal-comment-add-resp
    [m/standard-middlewares]
    (fn [db [comment]]
      (update-in db [:fractals :fractal-detail :comments :items] #(u/concat-vec [comment] %))))

  (f/register-handler
    :fractal-comment-remove
    [m/standard-middlewares (f/undoable "comment-remove")]
    (fn [db [fractal-id comment-id]]
      (f/dispatch [:api-delete
                   {:api-route    :fractal-comment
                    :route-params {:id fractal-id :comment-id comment-id}
                    :error-undo?  true}])
      (u/remove-first-in db [:fractals :fractal-detail :comments :items] {:id comment-id})))

  (f/register-handler
    :fractals-sidebar-select
    m/standard-middlewares
    (s/fn [db [fractal :- fch/PublishedFractal]]
      (if-not (= (:id (fractal-detail db)) (:id fractal))
        (do
          (t/go! :fractal-detail :id (:id fractal))
          (assoc-fractal-detail db fractal))
        db)))

  (f/register-handler
    :fractal-fork
    m/standard-middlewares
    (s/fn [db [fractal :- fch/PublishedFractal]]
      (if-not (d/logged-user db)
        (do (d/snack-n-go! "Please log in first in order to fork a fractal" :login)
            db)
        (do
          (t/go! :fractal-create)
          (update-in db [:fractals :forms] #(merge % (select-keys fractal [:l-system :canvas])))))))

  (f/register-handler
    :fractal-remove
    [m/standard-middlewares (f/undoable "fractal-delete")]
    (s/fn [db [fractal :- fch/PublishedFractal]]
      (let [id-entry (u/select-key fractal :id)]
        (dialog/hide-dialog!)
        (f/dispatch [:api-delete
                     {:api-route    :fractal
                      :route-params id-entry
                      :error-undo?  true}])
        (u/remove-first-in db [:fractals :fractals-user :items] id-entry))))
  )




