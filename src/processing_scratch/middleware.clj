(ns processing-scratch.middleware
  (:require [quil.core :as q]))

(defn- key-pressed [initial-settings state event]
  (let [{key :raw-key} event
        settings (or (get state :controls) initial-settings)
        new-state (assoc state :controls settings)]
    (case key
      \f (do (q/save-frame "frames/######.png")
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
                     (user-key-pressed (key-pressed settings state event) event)))))
