(ns build
  (:require [clojure.tools.build.api :as b]))

(def lib 'me.fogus/reinen-vernunft)
(def description "Code conversations in Clojure regarding the application of pure search, reasoning, and query algorithms.")
;;(def version (format "0.0.%s" (b/git-count-revs nil)))
(def version "0.0.2")
(def class-dir "target/classes")
(def jar-file (format "target/%s.jar" (name lib)))

;; delay to defer side effects (artifact downloads)
(def basis (delay (b/create-basis {:project "deps.edn"})))

(defn clean [_]
  (b/delete {:path "target"}))

(defn jar [_]
  (b/write-pom {:class-dir class-dir
                :lib lib
                :version version
                :basis @basis
                :src-dirs ["src"]})
  (b/copy-dir {:src-dirs ["src" "resources"]
               :target-dir class-dir})
  (b/jar {:class-dir class-dir
          :jar-file jar-file}))
