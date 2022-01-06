(ns dev.mccue.jproject.build
  (:require [clojure.tools.build.api :as b]
            [clojure.tools.deps.alpha.util.dir :refer [with-dir]])
  (:refer-clojure :exclude [compile]))

(defn compile
  [& args]
  (println args)
  (let [basis (b/create-basis)]
    (println "Compiling Java")
    (let [class-dir "target/classes"]
      (b/delete {:path class-dir})
      (b/copy-dir {:src-dirs ["src"]
                   :target-dir "target/classes"})
      (b/javac
        {:src-dirs   ["src"]
         :class-dir  class-dir
         :basis      basis
         :javac-opts ["-source" "17" "-target" "17" "--enable-preview"]}))))