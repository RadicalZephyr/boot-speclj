(ns radicalzephyr.boot-speclj-test
  (:require [speclj.core :refer :all]))

(describe "tests"
  (it "work"
    (should= true true))

  (it "fail"
    (should= true false))

  (xit "pending"
    (should= true false)))
