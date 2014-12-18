(ns arkham-horror.game.active-test
  (:require [clojure.test :refer :all]
            [arkham-horror.game.active :refer :all]
            [arkham-horror.structure :as structure]
            [arkham-horror.phase :as phase]
            [arkham-horror.investigator :as investigator]
            [arkham-horror.investigator.dice :as dice]))

(defn rig-dice [pips]
  (dosync (ref-set active-game (structure/update-path @active-game [phase investigator dice]
                                                      (fn [dice] (assoc dice :value pips))))))

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
                                      "Stamina: 7/7"
                                      "Sanity:  3/3"
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
    (is (= (help) '[(reset)])))
  (testing "Cthulu ends the world"
    (reset)
    (with-out-str
      (begin {:ancient-one "Cthulu"
              :investigators ["Monterey Jack"]})
      (start-init)
      (init-investigator {:speed 2 :fight 5 :lore 4})
      (rig-dice 1)
      (advance-phase)
      (end-init))
    (is (= (with-out-str (awaken)) "Cthulu awakened"))
    (is (= (help) '[(start-upkeep)]))
    (dotimes [n 8]
      (with-out-str
        (when (not (= n 0)) (defend))
        (start-upkeep)
        (advance-phase)
        (end-upkeep)
        (start-attack)
        (attack)
        (accept-roll)
        (end-attack)))
    (is (= (with-out-str (defend))
           (clojure.string/join "\n" ["Monterey Jack"
                                      "Stamina: 1/1"
                                      "Sanity:  1/1"
                                      "Speed  1  <2>  3   4 "
                                      "Sneak  3  <2>  1   0 "
                                      "Fight  2   3   4  <5>"
                                      "Will   3   2   1  <0>"
                                      "Lore   1   2   3  <4>"
                                      "Luck   5   4   3  <2>"])))
    (is (= (help) '[(start-upkeep)]))
    (is (= (with-out-str (start-upkeep))
           (clojure.string/join "\n" ["Monterey Jack"
                                      "Stamina: 1/1"
                                      "Sanity:  1/1"
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
    (is (= (with-out-str (focus-investigator {:speed-sneak 2}))
           (clojure.string/join "\n" ["Monterey Jack"
                                      "Stamina: 1/1"
                                      "Sanity:  1/1"
                                      "Speed  1   2   3  <4>"
                                      "Sneak  3   2   1  <0>"
                                      "Fight  2   3   4  <5>"
                                      "Will   3   2   1  <0>"
                                      "Lore   1   2   3  <4>"
                                      "Luck   5   4   3  <2>"])))
    (is (= (help) '[(advance-phase)]))
    (is (= (with-out-str (advance-phase)) "Phase over"))
    (is (= (help) '[(end-upkeep)]))
    (is (= (with-out-str (end-upkeep)) "Investigators refreshed"))
    (is (= (help) '[(start-attack)]))
    (is (= (with-out-str (start-attack)) "Attack\nDoom track: 13"))
    (is (= (help) '[(attack)]))
    (is (= (with-out-str (attack)) "Roll: 1 1 1"))
    (is (= (help) '[(accept-roll) (exhaust-item <n>)]))
    (is (= (with-out-str (accept-roll)) "Attack\nDoom track: 13"))
    (is (= (help) '[(end-attack)]))
    (is (= (with-out-str (end-attack)) "Defend"))
    (is (= (help) '[(defend)]))
    (is (= (with-out-str (defend)) "Cthulu has destroyed the world!"))
    (is (= (help) '[(reset)])))
  (testing "Cthulu is defeated"
    (reset)
    (with-out-str
      (begin {:ancient-one "Cthulu"
              :investigators ["Monterey Jack"]})
      (start-init)
      (init-investigator {:speed 2 :fight 5 :lore 4})
      (rig-dice 6)
      (advance-phase)
      (end-init)
      (awaken)
      (dotimes [n 5]
        (start-upkeep)
        (advance-phase)
        (end-upkeep)
        (start-attack)
        (attack)
        (accept-roll)
        (end-attack)
        (defend))
      (start-upkeep)
      (advance-phase)
      (end-upkeep))
    (is (= (help) '[(start-attack)]))
    (is (= (with-out-str (start-attack)) "Attack\nDoom track: 3"))
    (is (= (with-out-str (attack)) "Roll: 6 6 6"))
    (is (= (with-out-str (accept-roll)) "Cthulu has been defeated!")))
  (testing "Bullwhip re-roll"
    (with-out-str
      (reset)
      (begin {:ancient-one "Cthulu"
              :investigators ["Monterey Jack"]})
      (start-init)
      (init-investigator {:speed 2 :fight 5 :lore 4})
      (rig-dice 1)
      (advance-phase)
      (end-init)
      (awaken)
      (start-upkeep)
      (attack)
      (rig-dice 6))
    (is (= (with-out-str (exhaust-item 1)) "Roll: 1 1 6"))
    (is (= (help) '[(accept-roll)]))))
