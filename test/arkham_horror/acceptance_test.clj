(ns arkham-horror.acceptance-test
  (:require [clojure.test :refer :all]
            [arkham-horror.core :refer :all]
            [arkham-horror.investigator :as investigator]))

(deftest azathoth-test
  (testing "Awakening ends world"
    (reset)
    (is (= (get-status) "To begin a game, type (begin <config>)"))
    (begin {:ancient-one "Azathoth"})
    (is (= (get-status) "Welcome to Arkham Horror!\nType (awaken) to rouse Azathoth."))
    (awaken)
    (is (= (get-status) "Azathoth has destroyed the world!\nType (reset) to start over."))))

(deftest investigator-test
  (testing "Basic initialize"
    (reset)
    (begin {:ancient-one "Azathoth"})
    (let [investigator (investigator/make "Monterey Jack" {:speed 3 :fight 3 :lore 4})]
      (is (= (:name @investigator) "Monterey Jack"))
      (is (= (:speed @investigator) 3))
      (is (= (:sneak @investigator) 1))
      (is (= (:fight @investigator) 3))
      (is (= (:will  @investigator) 2))
      (is (= (:lore  @investigator) 4))
      (is (= (:luck  @investigator) 2))))
  (testing "Cannot initialize after awakening"
    (reset)
    (begin {:ancient-one "Azathoth"})
    (awaken)
    (is (thrown? AssertionError (investigator/make "Monterey Jack" {:speed 2 :fight 2 :lore 2}))))
  (testing "Cannot initialize before beginning"
    (reset)
    (is (thrown? AssertionError (investigator/make "Monterey Jack" {:speed 2 :fight 2 :lore 2}))))
  (testing "Stat bounds"
    (reset)
    (begin {:ancient-one "Azathoth"})
    (is (thrown? AssertionError (investigator/make "Monterey Jack" {:speed 0 :fight 2 :lore 2})))
    (is (thrown? AssertionError (investigator/make "Monterey Jack" {:speed 5 :fight 2 :lore 2})))
    (is (thrown? AssertionError (investigator/make "Monterey Jack" {:speed 2 :fight 1 :lore 2})))
    (is (thrown? AssertionError (investigator/make "Monterey Jack" {:speed 2 :fight 6 :lore 2})))
    (is (thrown? AssertionError (investigator/make "Monterey Jack" {:speed 2 :fight 2 :lore 0})))
    (is (thrown? AssertionError (investigator/make "Monterey Jack" {:speed 2 :fight 2 :lore 5})))))
