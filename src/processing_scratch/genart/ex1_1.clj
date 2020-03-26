(ns processing-scratch.genart.ex1-1
  (:require [quil.core :as q]
            [quil.middleware :as m]))

(defn setup []
  (q/set-state!
    :noise-x (q/random 10)
    :noise-y (q/random 10)))

; the illusion of depth and shading is created by many overlapping edges
; the numbers in here were picked for a 2000x2000 image, so most will need to be tweaked when changinyg the screen size
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
        ; when centered around 0, -1/8 to 1/8 is 1/4 of the viewable area, before step & noise multiple are factored in
        xend (/ (q/width) screen-end)
        yend (/ (q/height) screen-end)
        xs (vec (range (- xend) (inc xend) step))
        ys (vec (range (- yend) (inc yend) step))
        ; multiplying by x/y position makes it more extreme at the edges
        ; steeper noise = more loose. more gradual noise = closer?
        draw-point (fn [x y noise]
                     (q/with-translation
                       [(* x noise 6) (* y noise 6)]
                       (let [diameter
                             (* noise 26)]
                         (q/ellipse 0, 0, diameter diameter))))
        ]
    (dorun (for [x (range (count xs))
                 y (range (count ys))]
             (draw-point (xs x) (ys y)
                         ; perlin noise, the x/y modifications here are just advancing along the noise plane
                         (q/noise (+ xnoise (* x 0.02))
                                  (+ ynoise (* y 0.02))))))))

(defn mouse-pressed [state _event]
  (assoc state
    :noise-x (q/random 1)
    :noise-y (q/random 1)))

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
