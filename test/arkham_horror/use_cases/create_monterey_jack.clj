(ns arkham-horror.use-cases.create-monterey-jack
  (:require [clojure.test :refer :all]
            [arkham-horror.investigator :as investigator]))

(deftest create-monterey-jack-example
  (testing "Primary Course"
    (let [monterey-jack (investigator/make "Monterey Jack" {:speed 2 :fight 2 :lore 2})]
      (is (= (investigator/name monterey-jack)  "Monterey Jack"))
      (is (= (investigator/speed monterey-jack) 2))
      (is (= (investigator/sneak monterey-jack) 2))
      (is (= (investigator/fight monterey-jack) 2))
      (is (= (investigator/will monterey-jack)  3))
      (is (= (investigator/lore monterey-jack)  2))
      (is (= (investigator/luck monterey-jack)  4)))))
