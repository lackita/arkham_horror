(ns arkham-horror.use-cases.investigator
  (:require [clojure.test :refer :all]
            [clojure.test.check.clojure-test :refer :all]
            [arkham-horror.generators :as gen]
            [arkham-horror.investigator :as investigator]))

(deftest meters
  (checking "Primary Course" [investigator gen/investigator]
    (dosync
     (investigator/decrement-maximum-sanity investigator)
     (is (= (investigator/maximum-sanity investigator)
            (dec (investigator/initial-maximum-sanity (investigator/name investigator)))))
     (is (= (investigator/maximum-sanity investigator) (investigator/sanity investigator)))
     (investigator/move-sanity investigator -2)
     (is (= (investigator/sanity investigator)
            (- (investigator/maximum-sanity investigator) 2)))
     (investigator/decrement-maximum-stamina investigator)
     (is (= (investigator/maximum-stamina investigator)
            (dec (investigator/initial-maximum-stamina (investigator/name investigator)))))
     (is (= (investigator/maximum-stamina investigator) (investigator/stamina investigator)))
     (investigator/move-stamina investigator -2)
     (is (= (investigator/stamina investigator)
            (- (investigator/maximum-stamina investigator) 2))))))
