(ns arkham-horror.investigator-test
  (:require [clojure.test :refer :all]
            [arkham-horror.investigator :as investigator]))

(def base-investigator (investigator/make {:speed 1
                                           :sneak 2
                                           :fight 3}))

(deftest speed-test
  (is (= (investigator/speed base-investigator) 1))
  (is (= (investigator/speed
          (investigator/speed-sneak-slider base-investigator -1))
         0)))

(deftest sneak-test
  (is (= (investigator/sneak base-investigator) 2))
  (is (= (investigator/sneak
          (investigator/speed-sneak-slider base-investigator 1))
         1)))

(deftest fight-test
  (is (= (investigator/fight base-investigator) 3))
  (is (= (investigator/fight
          (investigator/fight-will-slider base-investigator 1))
         4)))
