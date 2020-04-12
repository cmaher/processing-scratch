(ns processing-scratch.prelude
  (:require [quil.core :as q]))


(defmacro
  with-matrix
  "Performs body with a new matrix, restores current matrix on exit."
  [& body]
  `(do
     (q/push-matrix)
     (try
       ~@body
       (finally
         (q/pop-matrix)))))
