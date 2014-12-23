(ns arkham-horror.use-cases.monterey-jack
  (:require [clojure.test :refer :all]
            [clojure.test.check :as tc]
            [clojure.test.check.clojure-test :refer :all]
            [clojure.test.check.generators :as gen]
            [arkham-horror.investigator :as investigator]
            [arkham-horror.card :as card]))

(defn belonging? [investigator card-name]
  (some #(= (card/name %) card-name) (investigator/cards investigator)))

(defmacro assertion-error? [expression]
  `(try ~expression false (catch AssertionError ~(gensym "e") true)))

(deftest create
  (checking "Primary Course" [speed (gen/choose 1 4)
                              fight (gen/choose 2 5)
                              lore  (gen/choose 1 4)
                              decision (gen/vector (gen/choose 0 2) 2)]
    (let [monterey-jack (investigator/make "Monterey Jack" {:speed speed :fight fight :lore  lore})]
      (is (not (empty? (investigator/pending-decision monterey-jack))))
      (is (= (count (investigator/unique-items monterey-jack)) 0))
      (investigator/make-decision monterey-jack decision)
      (is (empty? (investigator/pending-decision monterey-jack)))
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
      (is (= (investigator/money monterey-jack) 7))
      (is (= (investigator/clue-tokens monterey-jack) 1))
      (is (= (count (investigator/cards monterey-jack)) 5))
      (is (belonging? monterey-jack "Bullwhip"))
      (is (belonging? monterey-jack ".38 Revolver"))
      (is (= (count (investigator/unique-items monterey-jack)) 2))
      (is (= (count (investigator/common-items monterey-jack)) 2))
      (is (= (count (investigator/skills monterey-jack)) 1))
      (is (= (investigator/maximum-sanity monterey-jack) 3))
      (is (= (investigator/maximum-stamina monterey-jack) 7))))
  (checking "Exceptional Course: Speed Below Range" [speed gen/neg-int]
    (is (assertion-error? (investigator/make "Monterey Jack" {:speed speed :fight 2 :lore 2}))))
  (checking "Exceptional Course: Speed Above Range" [speed (gen/fmap #(+ % 5) gen/pos-int)]
    (is (assertion-error? (investigator/make "Monterey Jack" {:speed speed :fight 2 :lore 2}))))
  (checking "Exceptional Course: Fight Below Range" [fight (gen/fmap inc gen/neg-int)]
    (is (assertion-error? (investigator/make "Monterey Jack" {:speed 2 :fight fight :lore 2}))))
  (checking "Exceptional Course: Fight Above Range" [fight (gen/fmap #(+ % 6) gen/pos-int)]
    (is (assertion-error? (investigator/make "Monterey Jack" {:speed 2 :fight fight :lore 2}))))
  (checking "Exceptional Course: Lore Below Range" [lore gen/neg-int]
    (is (assertion-error? (investigator/make "Monterey Jack" {:speed 2 :fight 2 :lore lore}))))
  (checking "Exceptional Course: Lore Above Range" [lore (gen/fmap #(+ % 5) gen/pos-int)]
    (is (assertion-error? (investigator/make "Monterey Jack" {:speed 2 :fight 2 :lore lore}))))
  (checking "Exceptional Course: Decision Above Range"
    [bad-decision (gen/vector (gen/fmap #(+ % 3) gen/pos-int) 2)]
    (is (assertion-error?
         (investigator/make-decision
          (investigator/make "Monterey Jack" {:speed 2 :fight 2 :lore 2})
          bad-decision))))
  (checking "Exceptional Course: Decision Below Range"
    [bad-decision (gen/vector gen/s-neg-int 2)]
    (is (assertion-error?
         (investigator/make-decision
          (investigator/make "Monterey Jack" {:speed 2 :fight 2 :lore 2})
          bad-decision))))
  (checking "Exception Course: Too Few Decisions"
    [bad-decision (gen/vector (gen/choose 0 2) 1)]
    (is (assertion-error?
         (investigator/make-decision
          (investigator/make "Monterey Jack" {:speed 2 :fight 2 :lore 2})
          bad-decision))))
  (checking "Exceptional Course: Too Many Decisions"
    [bad-decision (gen/vector (gen/choose 0 2) 3)]
    (is (assertion-error?
         (investigator/make-decision
          (investigator/make "Monterey Jack" {:speed 2 :fight 2 :lore 2})
          bad-decision)))))
