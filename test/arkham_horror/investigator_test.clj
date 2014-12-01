(ns arkham-horror.investigator-test
  (:require [clojure.test :refer :all]
            [arkham-horror.investigator :as investigator]
            [arkham-horror.stat :as stat]))

(def base-investigator (investigator/make {:speed 1 :sneak 2
                                           :fight 3 :will  4
                                           :lore  5 :luck  6
                                           :focus 2}))
(def monterey-jack (investigator/init (investigator/make "Monterey Jack")
                                      {:speed 3 :fight 2 :lore 2}))


(defn valid-slider? [stat value
                     slider delta new-value]
  (and (is (= (stat monterey-jack) value))
       (is (= (stat (slider monterey-jack delta)) new-value))))

(deftest sliders-test
  (is (valid-slider? stat/speed 3
                     stat/speed-sneak-slider -2 1))
  (is (valid-slider? stat/sneak 1
                     stat/speed-sneak-slider -2 3))
  (is (valid-slider? stat/fight 2
                     stat/fight-will-slider 1 3))
  (is (valid-slider? stat/will 3
                     stat/fight-will-slider -1 3))
  (is (valid-slider? stat/lore 2
                     stat/lore-luck-slider 1 3))
  (is (valid-slider? stat/luck 4
                     stat/lore-luck-slider -1 5)))

(deftest focus-test
  (is (= (stat/speed (investigator/focus monterey-jack {:speed-sneak 1})) 4))
  (is (= (stat/sneak (investigator/focus monterey-jack {:speed-sneak 2})) 0))
  (is (= (stat/fight (investigator/focus monterey-jack {:fight-will  1})) 3))
  (is (= (stat/will  (investigator/focus monterey-jack {:fight-will  2})) 1))
  (is (= (stat/lore  (investigator/focus monterey-jack {:lore-luck   1})) 3))
  (is (= (stat/luck  (investigator/focus monterey-jack {:lore-luck   2})) 2))
  (is (thrown? AssertionError (-> monterey-jack
                                  (investigator/focus {:lore-luck 2})
                                  (investigator/focus {:lore-luck 2}))))
  (is (thrown? AssertionError (investigator/focus base-investigator
                                                  {:speed-sneak 3}))))

(deftest monterey-jack-test
  (is (= (stat/speed (investigator/init monterey-jack {:speed 2 :fight 3 :lore 1})) 2))
  (is (= (stat/speed (investigator/init monterey-jack {:speed 3 :fight 3 :lore 1})) 3))
  (is (= (stat/sneak (investigator/init monterey-jack {:speed 2 :fight 3 :lore 1})) 2))
  (is (= (stat/sneak (investigator/init monterey-jack {:speed 1 :fight 3 :lore 1})) 3))
  (is (= (stat/fight (investigator/init monterey-jack {:speed 1 :fight 3 :lore 1})) 3))
  (is (= (stat/fight (investigator/init monterey-jack {:speed 1 :fight 2 :lore 1})) 2))
  (is (= (stat/will  (investigator/init monterey-jack {:speed 1 :fight 3 :lore 1})) 2))
  (is (= (stat/will  (investigator/init monterey-jack {:speed 1 :fight 2 :lore 1})) 3))
  (is (= (stat/lore  (investigator/init monterey-jack {:speed 1 :fight 2 :lore 1})) 1))
  (is (= (stat/lore  (investigator/init monterey-jack {:speed 1 :fight 2 :lore 2})) 2))
  (is (= (stat/luck  (investigator/init monterey-jack {:speed 1 :fight 2 :lore 1})) 5))
  (is (= (stat/luck  (investigator/init monterey-jack {:speed 1 :fight 2 :lore 2})) 4))
  (is (= (stat/maximum-sanity monterey-jack) 3))
  (is (= (stat/maximum-stamina monterey-jack) 7)))
