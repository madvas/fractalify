(ns fractalify.main.api-routes)

(defn get-routes []
  ["/" [["public/" [[true :static]]]
        ["api/contact" :contact]
        [true :main]]])