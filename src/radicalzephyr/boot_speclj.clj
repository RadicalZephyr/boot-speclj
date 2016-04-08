(ns radicalzephyr.boot-speclj
  {:boot/export-tasks true}
  (:require [boot.core :as core :refer [deftask]]
            [speclj.cli :as cli]))

(deftask spec
  "Run speclj tests in a pod."
  []
  (core/with-pass-thru [fs]
    (cli/do-specs {:specs ["spec"]
                   :runner "standard"
                   :reporters ["progress"]
                   :tags []
                   :omit-pending false})))
