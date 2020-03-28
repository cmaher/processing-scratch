(ns processing-scratch.genart.ex1-1
  (:require [quil.core :as q]
            [quil.middleware :as m]
            [processing-scratch.rnd :as r]))

; Generative art, example i-1, modified

(defn setup []
  (q/frame-rate 60)
  (q/color-mode :hsb 100)
  (q/set-state!
    :noise-x (q/random 100)
    :noise-y (q/random 100)
    :noise-z (q/random 100)
    :tx-x-noise (q/random 100)
    :tx-y-noise (q/random 100)
    :noise-hue-lo (q/random 100)
    :noise-hue-step (q/random 100)
    :max-diameter 24))

(defn update-state [state]
  (assoc state
    ; maybe base these off of size of canvas
    :noise-z (+ (:noise-z state) 0.005)
    :noise-hue-lo (+ (:noise-hue-lo state) 0.01)
    :noise-hue-step (+ (:noise-hue-step state) 0.01)
    :tx-x-noise (+ (:tx-x-noise state) 0.0009)
    :tx-y-noise (+ (:tx-y-noise state) 0.0009)))

; TODO
; Replace random with pcg random
; screen capture
; gif

; To try
; color always pulsating outward
; vary s/b of poin
; shift center of hue over time so it does not always look like a circle

; the illusion of depth and shading is created by many overlapping edges
; which is evident by making a larger step
(defn draw [state]
  (when (> (q/frame-count) 600)
    (q/exit))

  ; center the coordinates around the middle of the screen
  (let [{noise-x :noise-x
         noise-y :noise-y
         noise-z :noise-z
         noise-hue-lo :noise-hue-lo
         noise-hue-step :noise-hue-step
         max-diameter :max-diameter} state
        spacing 3.5

        ; hue is circular. must modulo 100 when reading
        hue-lo (* (q/noise noise-hue-lo) 200)
        hue-step (* (q/noise noise-hue-step) 300)

        tx-x (/ (q/width) 2)
        tx-y (/ (q/height) 2)
        ;tx-x (+ (* (q/noise (:tx-x-noise state)) (/ (q/width) 8) 4)
        ;        (/ (q/width) 4))
        ;tx-y (+ (* (q/noise (:tx-y-noise state)) (/ (q/height) 8) 4)
        ;        (/ (q/height) 4))
        screen-end 8
        step 3
        ; This is the main value to tweak when changing the canvas size
        ; Original value is .02 for a 20000-size square.
        ; assumes canvas is a square.
        noise-step (/ 40 (q/width))
        ; when centered around 0, -1/8 to 1/8 is 1/4 of the viewable area, before step & noise multiple are factored in
        xend (/ (q/width) screen-end)
        yend (/ (q/height) screen-end)
        xs (vec (range (- xend) (inc xend) step))
        ys (vec (range (- yend) (inc yend) step))
        ; distance from center, which will always be (0,0), or very close
        dist (fn [x y]
               (q/sqrt (+ (* x x)
                          (* y y))))
        max-dist (dist (* (last xs) spacing) (* (last ys) spacing))

        ; multiplying by x/y position makes it more extreme at the edges
        ; steeper noise = more loose. more gradual noise = closer?
        draw-point (fn [x-idx y-idx noise]
                     (let [x (* (xs x-idx) noise spacing)
                           y (* (ys y-idx) noise spacing)
                           diameter (* noise max-diameter)
                           dist-percent (/ (dist x y) max-dist)
                           hue (mod (+ hue-lo (* hue-step dist-percent noise))
                                    100)]
                       (q/fill hue 100 100 80)
                       ;(q/stroke 0 0 0 (* 50 (- 1 dist-percent)))
                       (q/stroke 0 25)
                       (q/with-translation
                         [x y]
                         ; an interesting variation is adding x/y to the diameter given
                         (q/ellipse 0 0 diameter diameter))))
        ]
    (q/color-mode :hsb 100)
    (q/background 80)
    (q/with-translation
      [tx-x tx-y]
      (dorun (for [x-idx (range (count xs))
                   y-idx (range (count ys))]
               (draw-point x-idx y-idx
                           ; the x/y modifications here are just advancing along the noise plane
                           (r/simplex (+ noise-x (* x-idx noise-step))
                                    (+ noise-y (* y-idx noise-step))
                                    noise-z)))))
    (q/save-frame "frames/#####.png")
    ))

(defn mouse-pressed [state _event]
  (if (= (q/width) 512)
    (q/resize-sketch 1024 1024)
    (q/resize-sketch 512 512))
  state)

(defn ex1-1 []
  (q/defsketch
    genart-1-1-app
    :title "such example"
    :size [1024 1024]
    :renderer :p3d
    :setup setup
    :mouse-pressed mouse-pressed
    :update update-state
    :draw draw
    :middleware [m/fun-mode]
    :resizable true
    :features [:keep-on-top]))
