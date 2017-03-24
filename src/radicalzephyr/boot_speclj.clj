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
  [c clojure   VERSION   str     "The version of Clojure for testing."
   p paths     PATH      #{str}  "Conj onto the set of paths to run specs from"
   r runner    RUNNER    str     "The name of the runner to use"
   R reporters REPORTERS #{str}  "Conj onto the set of reporters to use"
   t tags      TAG       #{str}  "Conj onto the set of tags to run"
   o omit                bool    "Flag indicating whether to omit pending specs"]
  (let [paths     (or (seq paths)     ["spec"])
        runner    (or runner           "standard")
        reporters (or (seq reporters) ["progress"])
        pod-deps (update-in (core/get-env) [:dependencies]
                            (fn [deps]
                              (cond->> (into deps (pod-deps))
                                clojure (mapv (partial replace-clojure-version clojure)))))

        pods (pod/pod-pool pod-deps :init init)]
    (core/cleanup (pods :shutdown))
    (core/with-pass-thru [fs]
      (pod/with-eval-in (pods)
        (cli/do-specs {:specs ~(vec paths)
                       :runner ~runner
                       :reporters ~(vec reporters)
                       :tags ~tags
                       :omit-pending ~omit}))
      (pods :refresh))))
