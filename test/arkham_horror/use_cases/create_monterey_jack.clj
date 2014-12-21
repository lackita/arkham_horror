(ns arkham-horror.use-cases.create-monterey-jack
  (:require [clojure.test :refer :all]
            [clojure.test.check.clojure-test :refer :all]
            [clojure.test.check.properties :as prop]
            [clojure.test.check.generators :as gen]
            [arkham-horror.investigator :as investigator]
            [arkham-horror.card :as card]))

(defn belonging? [investigator card-name]
  (some #(= (card/name %) card-name) (investigator/cards investigator)))

(defmacro assertion-error? [expression]
  `(try ~expression false (catch AssertionError ~(gensym "e") true)))

(defspec create-monterey-jack-gen-example
  (testing "Primary Course"
    (prop/for-all [speed (gen/choose 1 4)
                   fight (gen/choose 2 5)
                   lore  (gen/choose 1 4)]
                  (let [monterey-jack (investigator/make "Monterey Jack" {:speed speed
                                                                          :fight fight
                                                                          :lore  lore})]
                    (is (not (empty? (investigator/pending-decisions monterey-jack))))
                    (is (= (count (investigator/unique-items monterey-jack)) 0))
                    (investigator/make-decision monterey-jack [0 1])
                    (is (empty? (investigator/pending-decisions monterey-jack)))
                    (is (= (investigator/name monterey-jack) "Monterey Jack"))
                    (is (= (investigator/sanity monterey-jack) 3))
                    (is (= (investigator/stamina monterey-jack) 7))
                    (is (= (investigator/speed monterey-jack) speed))
                    (is (= (+ (investigator/speed monterey-jack)
                              (investigator/sneak monterey-jack))
                           4))
                    (is (= (investigator/fight monterey-jack) fight))
                    (is (= (+ (investigator/fight monterey-jack)
                              (investigator/will monterey-jack))
                           5))
                    (is (= (investigator/lore monterey-jack) lore))
                    (is (= (+ (investigator/lore monterey-jack)
                              (investigator/luck monterey-jack))
                           6))
                    (is (= (investigator/money monterey-jack)                7))
                    (is (= (investigator/clue-tokens monterey-jack)          1))
                    (is (= (count (investigator/cards monterey-jack))        5))
                    (is (belonging? monterey-jack                            "Bullwhip"))
                    (is (belonging? monterey-jack                            ".38 Revolver"))
                    (is (= (count (investigator/unique-items monterey-jack)) 2))
                    (is (= (count (investigator/common-items monterey-jack)) 2))
                    (is (= (count (investigator/skills monterey-jack))       1)))))
  (testing "Exceptional Course: Speed Below Range"
    (prop/for-all [speed gen/neg-int]
                  (is (assertion-error? (investigator/make "Monterey Jack" {:speed speed
                                                                            :fight 2
                                                                            :lore 2})))))
  (testing "Exceptional Course: Speed Above Range"
    (prop/for-all [speed (gen/such-that #(> % 4) gen/pos-int)]
                  (is (assertion-error? (investigator/make "Monterey Jack" {:speed speed
                                                                            :fight 2
                                                                            :lore 2}))))))
