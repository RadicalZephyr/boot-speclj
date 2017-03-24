(set-env!
 :source-paths #{"src"}
 :dependencies '[[org.clojure/clojure     "1.8.0"           :scope "provided"]
                 [boot/core               "2.5.5"           :scope "provided"]
                 [radicalzephyr/bootlaces "0.1.15-SNAPSHOT" :scope "test"]])


(require '[radicalzephyr.bootlaces :refer :all]
         '[radicalzephyr.boot-speclj :refer [spec]])

(def +version+ "0.2.0")

(bootlaces! +version+)

(task-options!
 pom {:project 'radicalzephyr/boot-speclj
      :version +version+
      :description "Run speclj tests in boot."
      :url "https://github.com/radicalzephyr/boot-speclj"
      :scm {:url "https://github.com/radicalzephyr/boot-speclj"}
      :license {"MIT License"
                "http://www.opensource.org/licenses/mit-license.php"}})
