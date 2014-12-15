(ns arkham-horror.game.active-test
  (:require [clojure.test :refer :all]
            [arkham-horror.game.active :refer :all]))

(deftest full-game-test
  (testing "Azathoth ends the world"
    (reset)
    (is (= (help) '[(begin <config>)]))
    (is (= (with-out-str (begin {:ancient-one "Azathoth"
                                 :investigators ["Monterey Jack"]}))
           "Welcome to Arkham Horror!"))
    (is (= (help) '[(start-init)]))
    (is (= (with-out-str (start-init)) "Initialization started"))
    (is (= (help) '[(init-investigator {:speed <speed> :fight <fight> :lore <lore>})]))
    (is (= (with-out-str (init-investigator {:speed 2 :fight 3 :lore 4}))
           (clojure.string/join "\n" ["Monterey Jack initialized"
                                      "Speed  1  <2>  3   4 "
                                      "Sneak  3  <2>  1   0 "
                                      "Fight  2  <3>  4   5 "
                                      "Will   3  <2>  1   0 "
                                      "Lore   1   2   3  <4>"
                                      "Luck   5   4   3  <2>"])))
    (is (= (help) '[(advance-phase)]))
    (is (= (with-out-str (advance-phase)) ""))
    (is (= (help) '[(end-init)]))
    (is (= (with-out-str (end-init)) "Investigators initialized"))
    (is (= (help) '[(awaken)]))
    (is (= (with-out-str (awaken)) "You lose"))
    (is (= (help) []))))
