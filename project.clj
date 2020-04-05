(defproject processing-scratch "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :repositories {"jcenter" "https://jcenter.bintray.com"}
  :dependencies [[org.clojure/clojure "1.10.1"]
                 [quil "3.1.0"]
                 [org.clojure/data.generators "0.1.2"]
                 [com.github.kilianB/pcg-java-8 "1.0.1"]
                 [amalloy/ring-buffer "1.3.1"]]
  :java-cmd "java8.bat"
  :java-source-paths ["java"]
  :profiles {:dev {:source-paths ["dev"]
                   :dependencies [[org.clojure/tools.namespace "0.3.1"]]}})
