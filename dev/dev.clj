(ns dev
  (:require
    [clojure.string :as string]
    [clojure.repl :refer [doc pst]]
    [clojure.test :refer [run-all-tests]]
    [clojure.tools.namespace.repl :refer [refresh refresh-all]]
    [clojure.spec.alpha :as spec]

    [processing-scratch.genart.ex1-1 :refer [ex1-1]]))

(spec/check-asserts true)


(defn repl-tests []
  (spec/check-asserts true)
  (run-all-tests #"processing-scratch.*"))
