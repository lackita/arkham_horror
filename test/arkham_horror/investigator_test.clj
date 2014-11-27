(ns arkham-horror.investigator-test
  (:require [clojure.test :refer :all]
            [arkham-horror.investigator :as investigator]
            [arkham-horror.stat :as stat]))

(def base-investigator (investigator/make {:speed 1 :sneak 2
                                           :fight 3 :will  4
                                           :lore  5 :luck  6
                                           :focus 2}))

(defn valid-slider? [stat value
                     slider delta new-value]
  (and (= (stat base-investigator) value)
       (= (stat (slider base-investigator delta)) new-value)))

(deftest sliders-test
  (is (valid-slider? stat/speed 1
                     stat/speed-sneak-slider -2 0))
  (is (valid-slider? stat/sneak 2
                     stat/speed-sneak-slider -2 3))
  (is (valid-slider? stat/fight 3
                     stat/fight-will-slider 1 4))
  (is (valid-slider? stat/will 4
                     stat/fight-will-slider -1 5))
  (is (valid-slider? stat/lore 5
                     stat/lore-luck-slider 1 6))
  (is (valid-slider? stat/luck 6
                     stat/lore-luck-slider -1 7)))

(deftest focus-test
  (is (= (stat/speed (investigator/focus base-investigator {:speed-sneak 1})) 2))
  (is (= (stat/sneak (investigator/focus base-investigator {:speed-sneak 2})) 0))
  (is (= (stat/fight (investigator/focus base-investigator {:fight-will  1})) 4))
  (is (= (stat/will  (investigator/focus base-investigator {:fight-will  2})) 2))
  (is (= (stat/lore  (investigator/focus base-investigator {:lore-luck   1})) 6))
  (is (= (stat/luck  (investigator/focus base-investigator {:lore-luck   2})) 4))
  (is (thrown? AssertionError (investigator/focus base-investigator
                                                  {:speed-sneak 3}))))

(deftest monterey-jack-test
  (is (= (stat/speed (investigator/make :monterey-jack {:speed 2 :fight 3 :lore 1})) 2))
  (is (= (stat/speed (investigator/make :monterey-jack {:speed 3 :fight 3 :lore 1})) 3))
  (is (= (stat/sneak (investigator/make :monterey-jack {:speed 2 :fight 3 :lore 1})) 2))
  (is (= (stat/sneak (investigator/make :monterey-jack {:speed 1 :fight 3 :lore 1})) 3))
  (is (= (stat/fight (investigator/make :monterey-jack {:speed 1 :fight 3 :lore 1})) 3))
  (is (= (stat/fight (investigator/make :monterey-jack {:speed 1 :fight 2 :lore 1})) 2))
  (is (= (stat/will  (investigator/make :monterey-jack {:speed 1 :fight 3 :lore 1})) 2))
  (is (= (stat/will  (investigator/make :monterey-jack {:speed 1 :fight 2 :lore 1})) 3))
  (is (= (stat/lore  (investigator/make :monterey-jack {:speed 1 :fight 2 :lore 1})) 1))
  (is (= (stat/lore  (investigator/make :monterey-jack {:speed 1 :fight 2 :lore 2})) 2))
  (is (= (stat/luck  (investigator/make :monterey-jack {:speed 1 :fight 2 :lore 1})) 5))
  (is (= (stat/luck  (investigator/make :monterey-jack {:speed 1 :fight 2 :lore 2})) 4)))
