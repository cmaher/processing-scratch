(ns processing-scratch.middleware
  (:require [quil.core :as q]))

(defn- controls-key-pressed [initial-settings state event]
  (let [{key :raw-key} event
        settings (or (get state :controls) initial-settings)
        new-state (assoc state :controls settings)]
    (case key
      \f (do (q/save-frame "captures/snapshots/######.png")
             new-state)
      \r (do
           (if (:is-small settings)
             (apply q/resize-sketch (:large-size settings [1024 1024]))
             (apply q/resize-sketch (:small-size settings [512 512])))
           (update-in new-state [:controls :is-small] not))
      new-state)))

; Requires fun-mode
(defn controls [options]
  (let [settings (merge {:small-size [512 512]
                         :large-size [1024 1024]
                         :is-small true}
                        (get options :controls))
        user-key-pressed (:key-pressed options (fn [state _] state))]
    (assoc options
      :size (:size options (:small-size settings))
      :key-pressed (fn [state event]
                     (user-key-pressed (controls-key-pressed settings state event) event)))))

(defn- gif-draw [settings]
  (when (>= (q/frame-count) (:start-frame settings))
    (let [gif-frame (- (q/frame-count) (:start-frame settings))
          fmt-frame (format "%06d" gif-frame)]
      (q/save-frame (str "captures/gif/" fmt-frame ".png"))))
  (when (>= (q/frame-count) (:max-frame settings))
    (q/exit)))

(defn gif [options]
  (let [settings (merge {:start-frame 0
                         :max-frame Long/MAX_VALUE}
                        (get options :gif))
        user-draw (:draw options (fn [_]))]
    (assoc options
      :draw (fn [state]
              (user-draw state)
              (gif-draw settings)))))
