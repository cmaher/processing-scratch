(ns processing-scratch.rnd
  (:require [quil.core :as q]
            [clojure.data.generators :as generators])
  (:import [org.stegu.noise SimplexNoise]
           [com.github.kilianB.pcg.fast PcgRSFast]))

(defn- constrain-simplex [noise]
  (/ (+ noise 1) 2))

(defn simplex
  ([x y] (constrain-simplex (SimplexNoise/noise x y)))
  ([x y z] (constrain-simplex (SimplexNoise/noise x y z)))
  ([x y z w] (constrain-simplex (SimplexNoise/noise x y z w))))

(defn uniform
  (^double [rng hi]
   (* hi (.nextDouble rng)))
  ([rng lo hi]
   (binding [generators/*rnd* rng]
     (if (= lo hi)
       lo
       (generators/uniform lo hi)))))

(defn new-rng
  ([seed state]
   (PcgRSFast. seed state))
  ([seed]
   (PcgRSFast. seed 0))
  ([]
   (PcgRSFast.)))
