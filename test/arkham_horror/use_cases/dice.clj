(ns arkham-horror.use-cases.dice
  (:require [clojure.test :refer :all]
            [clojure.test.check.clojure-test :refer :all]
            [clojure.test.check.generators :as gen]
            [arkham-horror.dice :as dice]))

(deftest roll
  (checking "Primary Course" [times gen/pos-int]
    (is (= (count (dice/roll times)) times))
    (is (every? (set (range 1 7)) (dice/roll times)))))
