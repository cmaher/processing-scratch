(ns processing-scratch.genart.ex5-1
  (:require [quil.core :as q]
            [quil.middleware :as qm]
            [processing-scratch.prelude :refer :all]
            [processing-scratch.middleware :as middleware]))


(defn- settings []
  (q/smooth 16))

(defn- draw-noise-alpha [_state x y noise]
  (let [alpha (* 255 noise)]
    (q/stroke 0 alpha)
    (q/line x y (inc x) (inc y))))

; Shading comes from adding distance between squares for 2 reasons:
;   1. Squares that are 1 stroke width apart look extra dark, because instead of 2 directly overlapping strokes,
;     you have 2 strokes side-by-side.
;   2. When squares are a bit farther apart, things look grey, because the eyes fill this in. You can verify that it is
;     the eyes doing this by bringing your face in closer
(defn- draw-noise-squares [_state x y noise]
  (let [len (min 5 (* 10 noise))]
    (q/rect x y len len)))

(defn- draw-noise-lines [_state x y noise]
  (let [rotation (* noise q/TWO-PI)]
    (with-matrix
      (q/translate x y)
      (q/rotate rotation)
      (q/line 0 0 20 0))))

(defn draw-noise-clouds [_state x y noise]
  (let [grey (+ 150 (* noise 120))
        alpha (+ 150 (* noise 120))
        edge-size (* noise 35)]
    (with-matrix
      (q/translate x y)
      (q/rotate (* noise 540))
      (q/no-stroke)
      (q/fill grey alpha)
      (q/ellipse 0 0 edge-size (/ edge-size 2)))))

(defn- initial-state [draw-noise-key]
  (let [state {:start-x (q/random 1000)
               :start-y (q/random 1000)
               :noise-step-x 0.1
               :noise-step-y 0.1
               :step-x 5
               :step-y 5
               :default-background #(q/background 255)
               :default-stroke #(q/stroke 0)
               :default-fill #(q/fill 255)
               :draw-noise draw-noise-key}]
    (case draw-noise-key
      :alpha (assoc state
               :noise-step-x 0.01
               :noise-step-y 0.01
               :step-x 1
               :step-y 1)
      :lines (assoc state
               :default-stroke #(q/stroke 0 150))
      :clouds (assoc state
                :default-background #(q/background 0))
      state)))

(defn- draw-noise-grid [state]
  (let [xs (vec (range 0 (q/width) (:step-x state)))
        ys (vec (range 0 (q/height) (:step-y state)))
        noise-idx (fn [init step step-size]
                    (+ (state init) (* (state step) step-size)))
        draw-noise (case (:draw-noise state)
                     :alpha draw-noise-alpha
                     :squares draw-noise-squares
                     :lines draw-noise-lines
                     :clouds draw-noise-clouds
                     (fn [& _]))]
    ((:default-background state))
    ((:default-fill state))
    ((:default-stroke state))
    (dorun
      (for [x (range 0 (count xs))
            y (range 0 (count ys))]
        (let [pos-x (xs x)
              pos-y (ys y)
              noise-x (noise-idx :start-x :noise-step-x x)
              noise-y (noise-idx :start-y :noise-step-y y)
              noise (q/noise noise-x noise-y)]
          (draw-noise state pos-x pos-y noise))))))

; try trimetric noise grid?
(defn- setup [draw-noise-key]
  (let [seed (System/currentTimeMillis)
        state (assoc (initial-state draw-noise-key)
                :seed seed)]
    (println (str "Seed: " seed))
    (q/random-seed seed)
    (q/frame-rate 1)
    (apply q/set-state! (flatten (vec state)))))

(defn- update-state [state]
  state)

(defn- draw [state]
  (draw-noise-grid state))

(defn sketch [draw-noise-key]
  (q/defsketch
    sketch-app
    :title "processing-scratch.genart.ex5-1"
    :settings settings
    :setup (partial setup draw-noise-key)
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
