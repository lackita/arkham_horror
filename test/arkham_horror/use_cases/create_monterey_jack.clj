(ns arkham-horror.use-cases.create-monterey-jack
  (:require [clojure.test :refer :all]
            [arkham-horror.investigator :as investigator]
            [arkham-horror.card :as card]))

(defn belonging? [investigator card-name]
  (some #(= (card/name %) card-name) (investigator/cards investigator)))

(deftest create-monterey-jack-example
  (testing "Primary Course: Basic Stats"
    (let [monterey-jack (investigator/make "Monterey Jack" {:speed 2 :fight 2 :lore 2})]
      (is (not (empty? (investigator/pending-decisions monterey-jack))))
      (is (= (count (investigator/unique-items monterey-jack)) 0))
      (investigator/make-decision monterey-jack [0 1])
      (is (empty? (investigator/pending-decisions monterey-jack)))
      (is (= (investigator/name monterey-jack)                 "Monterey Jack"))
      (is (= (investigator/sanity monterey-jack)               3))
      (is (= (investigator/stamina monterey-jack)              7))
      (is (= (investigator/speed monterey-jack)                2))
      (is (= (investigator/sneak monterey-jack)                2))
      (is (= (investigator/fight monterey-jack)                2))
      (is (= (investigator/will monterey-jack)                 3))
      (is (= (investigator/lore monterey-jack)                 2))
      (is (= (investigator/luck monterey-jack)                 4))
      (is (= (investigator/money monterey-jack)                7))
      (is (= (investigator/clue-tokens monterey-jack)          1))
      (is (= (count (investigator/cards monterey-jack))        5))
      (is (belonging? monterey-jack                            "Bullwhip"))
      (is (belonging? monterey-jack                            ".38 Revolver"))
      (is (= (count (investigator/unique-items monterey-jack)) 2))
      (is (= (count (investigator/common-items monterey-jack)) 2))
      (is (= (count (investigator/skills monterey-jack))       1)))))
