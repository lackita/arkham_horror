(ns arkham-horror.use-cases.ancient-one
  (:require [clojure.test :refer :all]
            [clojure.test.check.clojure-test :refer :all]
            [arkham-horror.generators :as gen]
            [arkham-horror.ancient-one :as ancient-one]))

(deftest awaken
  (checking "Primary Course" [ancient-one gen/ancient-one]
    (dosync
     (is (not (ancient-one/awakened? ancient-one)))
     (ancient-one/awaken ancient-one)
     (is (ancient-one/awakened? ancient-one))))
  (checking "Exceptional Course: Ancient One Defeated" [ancient-one gen/ancient-one]
    (dosync
     (ancient-one/defeat ancient-one)
     (is (thrown? AssertionError (ancient-one/awaken ancient-one)))
     true))
  (checking "Exceptional Course: Multiple Awakenings" [ancient-one gen/ancient-one]
    (dosync
     (ancient-one/awaken ancient-one)
     (is (thrown? AssertionError (ancient-one/awaken ancient-one)))
     true)))
