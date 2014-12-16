(ns arkham-horror.game.active-test
  (:require [clojure.test :refer :all]
            [arkham-horror.game.active :refer :all]
            [arkham-horror.structure :as structure]
            [arkham-horror.phase :as phase]
            [arkham-horror.investigator :as investigator]
            [arkham-horror.investigator.dice :as dice]))

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
           (clojure.string/join "\n" ["Monterey Jack"
                                      "Speed  1  <2>  3   4 "
                                      "Sneak  3  <2>  1   0 "
                                      "Fight  2  <3>  4   5 "
                                      "Will   3  <2>  1   0 "
                                      "Lore   1   2   3  <4>"
                                      "Luck   5   4   3  <2>"])))
    (is (= (help) '[(advance-phase)]))
    (is (= (with-out-str (advance-phase)) "Phase over"))
    (is (= (help) '[(end-init)]))
    (is (= (with-out-str (end-init)) "Investigators initialized"))
    (is (= (help) '[(awaken)]))
    (is (= (with-out-str (awaken)) "Azathoth has destroyed the world!"))
    (is (= (help) [])))
  (testing "Cthulu ends the world"
    (reset)
    (is (= (help) '[(begin <config>)]))
    (is (= (with-out-str (begin {:ancient-one "Cthulu"
                                 :investigators ["Monterey Jack"]}))
           "Welcome to Arkham Horror!"))
    (is (= (help) '[(start-init)]))
    (is (= (with-out-str (start-init)) "Initialization started"))
    (is (= (help) '[(init-investigator {:speed <speed> :fight <fight> :lore <lore>})]))
    (is (= (with-out-str (init-investigator {:speed 2 :fight 5 :lore 4}))
           (clojure.string/join "\n" ["Monterey Jack"
                                      "Speed  1  <2>  3   4 "
                                      "Sneak  3  <2>  1   0 "
                                      "Fight  2   3   4  <5>"
                                      "Will   3   2   1  <0>"
                                      "Lore   1   2   3  <4>"
                                      "Luck   5   4   3  <2>"])))
    (send active-game #(structure/update-path % [phase investigator dice]
                                              (fn [dice] (assoc dice :value 1))))
    (is (= (help) '[(advance-phase)]))
    (is (= (with-out-str (advance-phase)) "Phase over"))
    (is (= (help) '[(end-init)]))
    (is (= (with-out-str (end-init)) "Investigators initialized"))
    (is (= (help) '[(awaken)]))
    (is (= (with-out-str (awaken)) "Cthulu awakened"))
    (is (= (help) '[(start-upkeep)]))
    (is (= (with-out-str (start-upkeep))
           (clojure.string/join "\n" ["Monterey Jack"
                                      "Speed  1  <2>  3   4 "
                                      "Sneak  3  <2>  1   0 "
                                      "Fight  2   3   4  <5>"
                                      "Will   3   2   1  <0>"
                                      "Lore   1   2   3  <4>"
                                      "Luck   5   4   3  <2>"])))
    (is (= (help) '[(focus-investigator {:speed-sneak <delta>
                                         :fight-will <delta>
                                         :lore-luck <delta>})
                    (advance-phase)]))
    (is (= (with-out-str (advance-phase)) "Phase over"))
    (is (= (help) '[(end-upkeep)]))))
