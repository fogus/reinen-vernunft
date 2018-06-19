(defproject fogus/reinen-vernunft "0.1.1-SNAPSHOT"
  :description "Code conversations in Clojure regarding the application of pure search, reasoning, and query algorithms."
  :url "https://github.com/fogus/reinen-vernunft"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.9.0"]
                 [org.clojure/spec.alpha "0.1.143"]
                 [org.clojure/core.unify "0.5.7"]]
  :profiles {:dev {:dependencies [[datascript "0.16.6"]]}})
