(ns arkham-horror.doom-track-test
  (:require [clojure.test :refer :all]
            [arkham-horror.ancient-one :as ancient-one]
            [arkham-horror.ancient-one.doom-track :as doom-track]
            [arkham-horror.setup :as setup]))

(deftest empty-test
  (is (= (doom-track/level (doom-track/empty (doom-track/make 0 13))) 0))
  (is (= (doom-track/level (doom-track/empty (doom-track/make 1 13))) 0)))

(deftest advance-test
  (is (= (doom-track/level (doom-track/retract (doom-track/make 0 13))) 0))
  (is (= (doom-track/level (doom-track/retract (doom-track/make 2 13))) 1)))
