(ns processing-scratch.genart.ex4-2
  (:require [quil.core :as q]
            [quil.middleware :refer [fun-mode navigation-2d]]
            [processing-scratch.middleware :as middleware]))

; without high anti-aliasing, diamond artifacts are extreme
; To make it even smoother, I could implement FXAA as a custom shader
;   https://forum.processing.org/two/discussion/12840/antialiasing-in-processing
;   http://quil.info/api/image/pixels#filter-shader
(defn settings []
  (q/smooth 16))

(defn setup []
  (let [seed (System/currentTimeMillis)]
    (println (str "Seed: " seed))
    (q/random-seed seed)
    (q/frame-rate 30)
    (q/no-fill)
    (q/background 255)
    (let [max-radius 550
          noise-rad (q/random 100)
          radius (inc (* (q/noise noise-rad) max-radius))]
      (q/set-state!
        :stroke 256
        :max-radius max-radius
        :fn-stroke dec
        :radius radius
        :noise-rad noise-rad
        :noise-ang (q/random 100)
        :noise-x (q/random 100)
        :noise-y (q/random 100)
        :angle -90))))

(defn update-state [state]
  (let [noise-rad (+ (:noise-rad state) 0.005)
        noise-ang (+ (:noise-ang state) 0.005)
        stroke (:stroke state)
        fn-stroke (cond (>= stroke 255) dec
                        (<= stroke 0) inc
                        :else (:fn-stroke state))]
    (assoc state :noise-rad noise-rad
                 :noise-ang noise-ang
                 :noise-x (+ (:noise-x state) 0.01)
                 :noise-y (+ (:noise-y state) 0.01)
                 :fn-stroke fn-stroke
                 :stroke (fn-stroke stroke)
                 :radius (inc (* (q/noise noise-rad) (:max-radius state)))
                 :angle (+ (:angle state)
                           (* (q/noise noise-ang) 6)
                           -3))))

(defn draw [state]
  (let [{radius :radius
         angle :angle
         stroke :stroke
         noise-x :noise-x
         noise-y :noise-y} state
        center-x (+ (/ (q/width) 2)
                    (* 100 (q/noise noise-x))
                    -50)
        center-y (+ (/ (q/height) 2)
                    (* 100 (q/noise noise-y))
                    -50)
        rad (q/radians angle)
        op-rad (+ rad q/PI)
        x1 (+ center-x (* radius (q/cos rad)))
        y1 (+ center-y (* radius (q/sin rad)))
        x2 (+ center-x (* radius (q/cos op-rad)))
        y2 (+ center-y (* radius (q/sin op-rad)))]
    (q/stroke stroke)
    (q/line x1 y1 x2 y2)))

(defn ex4-2 []
  (q/defsketch
    genart-4-2-app
    :title "wave clock"
    :settings settings
    :setup setup
    :update update-state
    :draw draw
    :middleware [fun-mode
                 middleware/controls]
    :controls {:small-size [512 512]
               :large-size [1024 1024]}
    :resizable true
    :features [:keep-on-top]))
