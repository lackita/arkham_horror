(ns arkham-horror.investigator-test
  (:require [clojure.test :refer :all]
            [arkham-horror.investigator :as investigator]
            [arkham-horror.stat :as stat]))

(def base-investigator (investigator/make {:speed (stat/make 0 1 3)
                                           :sneak (stat/make 0 2 3)
                                           :fight (stat/make 2 3 5)
                                           :will  (stat/make 2 4 5)
                                           :lore  (stat/make 4 5 7)
                                           :luck  (stat/make 4 6 7)}))

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
  (is (= (stat/luck  (investigator/focus base-investigator {:lore-luck   2})) 4)))
