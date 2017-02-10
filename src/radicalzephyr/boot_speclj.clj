(ns radicalzephyr.boot-speclj
  {:boot/export-tasks true}
  (:require [boot.core :as core :refer [deftask]]
            [boot.pod :as pod]))

(defn init [fresh-pod]
  (pod/require-in fresh-pod '[speclj.cli :as cli]))

(defn pod-deps []
  (remove pod/dependency-loaded?
          '[[speclj "3.3.2"]]))

(defn replace-clojure-version
  "Given a desired Clojure version and an artifact/version pair,
  return the artifact/version pair, updated if it was for Clojure."
  [new-version [artifact version :as dep]]
  (if (= 'org.clojure/clojure artifact) [artifact new-version] dep))

(deftask spec
  "Run speclj tests in a pod."
  [c clojure  VERSION  str      "the version of Clojure for testing."
   r runner   RUNNER   str      "the name of the runner to use"
   t tags     TAG      #{str}   "assoc into the set of tags to run"
   o omit              boolean  "whether to omit pending specs"]
  (let [pod-deps (update-in (core/get-env) [:dependencies]
                            (fn [deps]
                              (cond->> (into deps (pod-deps))
                                clojure (mapv (partial replace-clojure-version clojure)))))

        pods (pod/pod-pool pod-deps :init init)]
    (core/cleanup (pods :shutdown))
    (core/with-pass-thru [fs]
      (pod/with-eval-in (pods)
       (cli/do-specs {:specs ["spec"]
                      :runner "standard"
                      :reporters ["progress"]
                      :tags []
                      :omit-pending false}))
      (pods :refresh))))
