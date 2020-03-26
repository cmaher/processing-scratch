(ns processing-scratch.genart.ex1-1
  (:require [quil.core :as q]
            [quil.middleware :as m]))

(defn setup []
  (q/set-state!
    :noise-x (q/random 10)
    :noise-y (q/random 10)))

; the illusion of depth and shading is created by many overlapping edges
(defn draw [state]
  (q/background 150)
  (q/stroke 0 50)
  (q/fill 255 200)
  ; center the coordinates around the middle of the screen
  (q/translate [(/ (q/width) 2) (/ (q/height) 2)])
  (let [xnoise (:noise-x state)
        ynoise (:noise-y state)
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
        ; multiplying by x/y position makes it more extreme at the edges
        ; steeper noise = more loose. more gradual noise = closer?
        draw-point (fn [x y noise]
                     (q/with-translation
                       [(* x noise 4) (* y noise 4)]
                       (let [diameter (* noise 26)]
                         ; an interesting variation is adding x/y to the diameter given
                         (q/ellipse 0 0 diameter diameter))))
        ]
    (dorun (for [x (range (count xs))
                 y (range (count ys))]
             (draw-point (xs x) (ys y)
                         ; perlin noise, the x/y modifications here are just advancing along the noise plane
                         ; the noi
                         (q/noise (+ xnoise (* x noise-step))
                                  (+ ynoise (* y noise-step))))))))

(defn mouse-pressed [state _event]
  (assoc state
    :noise-x (q/random 10)
    :noise-y (q/random 10)))

(defn ex1-1 []
  (q/defsketch
    genart-1-1-app
    :title "Cool thing"
    :size [512 512]
    :renderer :p3d
    :setup setup
    :mouse-pressed mouse-pressed
    :draw draw
    :middleware [m/fun-mode]
    :resizable true
    :features [:keep-on-top]))
