(ns fractalify.main.api-routes)

(defn get-routes []
  ["/" [["public/" [[true :static]]]
        [true :main]]])