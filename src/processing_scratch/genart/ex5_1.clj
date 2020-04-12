(ns processing-scratch.genart.ex5-1
  (:require [quil.core :as q]
            [quil.middleware :as qm]
            [processing-scratch.middleware :as middleware]))


(defn- settings []
  (q/smooth 16))

; a new line is always drawn at the step-x offset of 5, therefore the grid looks orderly
; However, some areas can have an underlying rectangle that extends for up to 2 blocks, while the other block inside
; is smaller, givin a nested appearance to the background.
; Shading comes from adding distance between squares for 2 reasons:
;   1. Squares that are 1 stroke width apart look extra dark, because instead of 2 directly overlapping strokes,
;     you have 2 strokes side-by-side.
;   2. Whhen squares are a bit farther apart, things look grey, because the eyes fill this in. You can verify that it is
;     the eyes doing this by bringing your face in closer
(defn- draw-noise-squares [_state x y noise-x noise-y]
  (let [noise (q/noise noise-x noise-y)
        len (* 10 noise)]
    (q/rect x y len len)))

(defn- initial-state []
  {:start-x (q/random 1000)
   :start-y (q/random 1000)
   :noise-step-x 0.1
   :noise-step-y 0.1
   :step-x 5
   :step-y 5
   :draw-noise :squares})

(defn- draw-noise-grid [state]
  (let [xs (vec (range 0 (q/width) (:step-x state)))
        ys (vec (range 0 (q/height) (:step-y state)))
        noise-idx (fn [init step step-size]
                    (+ (state init) (* (state step) step-size)))
        draw-noise (case (:draw-noise state)
                     :squares draw-noise-squares
                     (fn [_]))]
    (q/background 255)
    (q/fill 255)
    (dorun
      (for [x (range 0 (count xs))
            y (range 0 (count ys))]
        (let [pos-x (xs x)
              pos-y (ys y)
              noise-x (noise-idx :start-x :noise-step-x x)
              noise-y (noise-idx :start-y :noise-step-y y)]
          (draw-noise state pos-x pos-y noise-x noise-y))))))

; try trimetric noise grid?
(defn- setup []
  (let [seed (System/currentTimeMillis)
        state (initial-state)]
    (println (str "Seed: " seed))
    (q/random-seed seed)
    (apply q/set-state! (flatten (vec state)))))

(defn- update-state [state]
  state)

(defn- draw [state]
  (draw-noise-grid state))

(defn sketch []
  (q/defsketch
    sketch-app
    :title "processing-scratch.genart.ex5-1"
    :settings settings
    :setup setup
    :update update-state
    :draw draw
    :size [300 300]
    :middleware [qm/fun-mode
                 qm/navigation-2d
                 middleware/controls]
    :controls {:small-size [300 300]
               :large-size [300 300]}
    :resizable true
    :features [:keep-on-top]))
