(ns arkham-horror.doom-track-test
  (:require [clojure.test :refer :all]
            [arkham-horror.doom-track :as doom-track]
            [arkham-horror.setup :as setup]))

(deftest empty-test
  (is (= (doom-track/level (doom-track/empty (setup/begin))) 0))
  (is (= (-> (setup/begin)
             doom-track/advance
             doom-track/empty
             doom-track/level)
         0)))
