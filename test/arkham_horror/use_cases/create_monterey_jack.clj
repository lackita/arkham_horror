(ns arkham-horror.use-cases.create-monterey-jack
  (:require [clojure.test :refer :all]
            [arkham-horror.investigator :as investigator]
            [arkham-horror.item :as item]))

(deftest create-monterey-jack-example
  (testing "Primary Course: Basic Stats"
    (let [monterey-jack (investigator/make "Monterey Jack" {:speed 2 :fight 2 :lore 2})]
      (is (= (investigator/name monterey-jack)         "Monterey Jack"))
      (is (= (investigator/sanity monterey-jack)       3))
      (is (= (investigator/stamina monterey-jack)      7))
      (is (= (investigator/speed monterey-jack)        2))
      (is (= (investigator/sneak monterey-jack)        2))
      (is (= (investigator/fight monterey-jack)        2))
      (is (= (investigator/will monterey-jack)         3))
      (is (= (investigator/lore monterey-jack)         2))
      (is (= (investigator/luck monterey-jack)         4))
      (is (= (investigator/money monterey-jack)        7))
      (is (= (investigator/clue-tokens monterey-jack)  1))
      (is (= (count (investigator/items monterey-jack)) 4))
      (is (some #(= (item/name %) "Bullwhip") (investigator/items monterey-jack)))
      (is (some #(= (item/name %) ".38 Revolver") (investigator/items monterey-jack)))
      (is (= (count (filter #(= (item/deck %) :unique)
                            (investigator/items monterey-jack)))
             2)))))
