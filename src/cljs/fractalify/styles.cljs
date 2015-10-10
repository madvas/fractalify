(ns fractalify.styles
  (:require [clojure.string :as s]
            [fractalify.utils :as u]
            [material-ui.core :as ui]))

(def w-100 {:width "100%"})
(def block {:display "block"})
(def text-center {:text-align "center"})
(def page-title
  {:white-space      "nowrap"
   :overflow         "hidden"
   :text-overflow    "ellipsis"
   :margin           0
   :padding-top      0
   :letter-spacing   0
   :font-size        "24px"
   :font-weight      400
   :color            "rgba(255, 255, 255, 0.87)"
   :line-height      "64px"
   :-webkit-box-flex 1
   :flex             1})

(def canvas-size
  {:width  600
   :height 600})

(def canvas-style
  {:width "100%" :height "100%"})

(defn transition
  ([duration easing] (transition "all" duration easing))
  ([property duration easing]
   {:-webkit-transition (s/join " " [property duration easing])
    :-moz-transition    (s/join " " [property duration easing])
    :-ms-transition     (s/join " " [property duration easing])
    :-o-transition      (s/join " " [property duration easing])
    :transition         (s/join " " [property duration easing])}))

(def canvas-refresh-indicator-wrap
  {:position     "absolute"
   :margin-left  "auto"
   :margin-right "auto"
   :left         0
   :right        0
   :top          "45%"
   :width        40
   :height       40})

(def canvas-refresh-indicator
  (merge
    (transition "opacity" "0.5s" "ease-in-out")))

(def canvas-paper-wrap
  {:position      :relative
   :margin-bottom 10})

(def comments-wrap
  {:min-height 400})

(def fractal-controls-shield
  {:position         :absolute
   :background-color :white
   :width            "100%"
   :height           "100%"
   :opacity          0.7
   :z-index          99})

(def fork-btn-wrap
  {:position :absolute
   :z-index  100
   :width    "100%"
   :height   "100%"})

(def header-btn
  {:background-color "transparent"
   :color            (ui/color :grey100)})

(def main-body
  {:background-color "#F2F2F2"
   :padding-top      20
   :display          :block
   :position         :relative
   :min-height       "90%"
   :padding-bottom   40
   })

(def pad-0 {:padding 0})
(def pad-5 {:padding 5})
(def pad-10 {:padding 10})
(def pad-15 {:padding 15})
(def pad-20 {:padding 20})

(def pad-lef-0 {:padding-left 0})
(def pad-lef-5 {:padding-left 5})
(def pad-lef-10 {:padding-left 10})
(def pad-lef-15 {:padding-left 15})
(def pad-lef-20 {:padding-left 20})

(def pad-rig-0 {:padding-right 0})
(def pad-rig-5 {:padding-right 5})
(def pad-rig-10 {:padding-right 10})
(def pad-rig-15 {:padding-right 15})
(def pad-rig-20 {:padding-right 20})

(def pad-top-0 {:padding-top 0})
(def pad-top-5 {:padding-top 5})
(def pad-top-10 {:padding-top 10})
(def pad-top-15 {:padding-top 15})
(def pad-top-20 {:padding-top 20})

(def pad-bot-0 {:padding-bottom 0})
(def pad-bot-5 {:padding-bottom 5})
(def pad-bot-10 {:padding-bottom 10})
(def pad-bot-15 {:padding-bottom 15})
(def pad-bot-20 {:padding-bottom 20})

(def pad-hor-0 (merge pad-rig-0 pad-lef-0))
(def pad-hor-5 (merge pad-rig-5 pad-lef-5))
(def pad-hor-10 (merge pad-rig-10 pad-lef-10))
(def pad-hor-15 (merge pad-rig-15 pad-lef-15))
(def pad-hor-20 (merge pad-rig-20 pad-lef-20))

(def pad-ver-0 (merge pad-bot-0 pad-top-0))
(def pad-ver-5 (merge pad-bot-5 pad-top-5))
(def pad-ver-10 (merge pad-bot-10 pad-top-10))
(def pad-ver-15 (merge pad-bot-15 pad-top-15))
(def pad-ver-20 (merge pad-bot-20 pad-top-20))

(def mar-0 {:margin 0})
(def mar-5 {:margin 5})
(def mar-10 {:margin 10})
(def mar-15 {:margin 15})
(def mar-20 {:margin 20})

(def mar-lef-0 {:margin-left 0})
(def mar-lef-5 {:margin-left 5})
(def mar-lef-10 {:margin-left 10})
(def mar-lef-15 {:margin-left 15})
(def mar-lef-20 {:margin-left 20})

(def mar-rig-0 {:margin-right 0})
(def mar-rig-5 {:margin-right 5})
(def mar-rig-10 {:margin-right 10})
(def mar-rig-15 {:margin-right 15})
(def mar-rig-20 {:margin-right 20})

(def mar-top-0 {:margin-top 0})
(def mar-top-5 {:margin-top 5})
(def mar-top-10 {:margin-top 10})
(def mar-top-15 {:margin-top 15})
(def mar-top-20 {:margin-top 20})

(def mar-bot-0 {:margin-bottom 0})
(def mar-bot-5 {:margin-bottom 5})
(def mar-bot-10 {:margin-bottom 10})
(def mar-bot-15 {:margin-bottom 15})
(def mar-bot-20 {:margin-bottom 20})

(def mar-hor-0 (merge mar-rig-0 mar-lef-0))
(def mar-hor-5 (merge mar-rig-5 mar-lef-5))
(def mar-hor-10 (merge mar-rig-10 mar-lef-10))
(def mar-hor-15 (merge mar-rig-15 mar-lef-15))
(def mar-hor-20 (merge mar-rig-20 mar-lef-20))

(def mar-ver-0 (merge mar-bot-0 mar-top-0))
(def mar-ver-5 (merge mar-bot-5 mar-top-5))
(def mar-ver-10 (merge mar-bot-10 mar-top-10))
(def mar-ver-15 (merge mar-bot-15 mar-top-15))
(def mar-ver-20 (merge mar-bot-20 mar-top-20))

(def tab-anchor (merge w-100 block pad-ver-15))
(def paper-block (merge pad-ver-20 pad-hor-10))
(def sidebar-wrap (merge paper-block {:min-height 1120}))