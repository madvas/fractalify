(ns fractalify.fractals.api-routes)

(defn get-routes
  ([] (get-routes false))
  ([with-methods?]
   ["/api/fractals" {["/" :id] [["/star" :fractal-star]
                                ["/comments" [[["/" :comment-id] (if with-methods?
                                                                   {:delete :fractal-comment-remove}
                                                                   :fractal-comment)]
                                              ["" (if with-methods?
                                                    {:post :fractal-comment-add
                                                     :get  :fractal-comments}
                                                    :fractal-comments)]]]

                                ["" (if with-methods?
                                      {:delete :fractal-remove
                                       :get    :fractal}
                                      :fractal)]]

                     ""        (if with-methods?
                                 {:put :fractal-add
                                  :get :fractals}
                                 :fractals)}]))
