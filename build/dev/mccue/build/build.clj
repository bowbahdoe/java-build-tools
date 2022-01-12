(ns dev.mccue.build.build
  (:require [clojure.tools.build.api :as b]
            [clojure.tools.deps.alpha.util.dir :refer [with-dir]])
  (:refer-clojure :exclude [compile]))

(defn compile
  [& args]
  (let [basis (b/create-basis)]
    (println "Compiling Java")
    (let [class-dir "target/classes"]
      (b/delete {:path class-dir})
      (b/javac
        {:src-dirs   ["src"]
         :class-dir  class-dir
         :basis      basis
         :javac-opts ["-source" "17" "-target" "17" "-g"]}))))