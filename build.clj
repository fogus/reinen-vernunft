(ns build
  (:require [clojure.tools.build.api :as b]))

(def lib 'fogus/reinen-vernunft)
(def description "Code conversations in Clojure regarding the application of pure search, reasoning, and query algorithms.")
(def version (format "0.1.%s" (b/git-count-revs nil)))
(def target-dir "target")
(def class-dir (str target-dir "/" "classes"))
(def jar-file (format "%s/%s-%s.jar" target-dir (name lib) version))
(def src ["src/clj"])
(def basis (b/create-basis {:project "deps.edn"}))

(defn clean
  "Delete the build target directory"
  [_]
  (b/delete {:path target-dir}))

(defn jar [_]
  (b/write-pom {:class-dir class-dir
                :lib lib
                :version version
                :src-pom "pom.xml"
                :basis basis
                :src-dirs src})
  (b/copy-dir {:src-dirs src
               :target-dir class-dir})
  (b/jar {:class-dir class-dir
          :jar-file jar-file}))
