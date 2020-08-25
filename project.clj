(defproject fogus/reinen-vernunft "0.1.1-SNAPSHOT"
  :description "Code conversations in Clojure regarding the application of pure search, reasoning, and query algorithms."
  :url "https://github.com/fogus/reinen-vernunft"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.10.1"]
                 [org.clojure/spec.alpha "0.2.187"]
                 [org.clojure/core.unify "0.5.7"]
                 [evalive "1.1.0"]]
  :profiles {:dev {:dependencies [[datascript "0.18.13"]]}})
