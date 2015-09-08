(ns fractalify.components.dialog-action
  (:require [material-ui.core :as ui]
            [reagent.core :as r]))

(defn dialog-action [form]
  (r/as-element
    [ui/mui-theme-wrap
     form]))
