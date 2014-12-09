(ns arkham-horror.dice-test
  (:require [clojure.test :refer :all]
            [arkham-horror.investigator.dice :as dice]))

(deftest make-test
  (is (= (dice/roll (dice/make 1)) 1))
  (is (= (dice/roll (dice/make 6)) 6))
  (is (every? (set (range 1 7)) (take 100 (repeatedly #(dice/roll (dice/make :random)))))))
