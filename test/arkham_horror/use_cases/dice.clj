(ns arkham-horror.use-cases.dice
  (:require [clojure.test :refer :all]
            [clojure.test.check.clojure-test :refer :all]
            [clojure.test.check.generators :as gen]
            [arkham-horror.dice :as dice]))

(deftest roll
  (checking "Primary Course" [times gen/pos-int]
    (is (= (count (dice/roll times)) times))
    (is (every? (set (range 1 7)) (dice/roll times)))))

(deftest count-successes
  (checking "Primary Course" [rolls (gen/fmap #(dice/roll %) gen/pos-int)]
    (is (= (count (filter #{5 6} rolls)) (dice/count-successes rolls)))))
