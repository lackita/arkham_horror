(ns arkham-horror.acceptance-test
  (:require [clojure.test :refer :all]
            [clojure.string :refer [join]]
            [arkham-horror.core :refer :all]
            [arkham-horror.board :as board]
            [arkham-horror.investigator :as investigator]))

(deftest azathoth-test
  (testing "Awakening ends world"
    (board/reset)
    (is (= (board/get-status) "Game not started.\nPhase: None\nCommands:\n\t(begin <config>)"))
    (begin {:ancient-one "Azathoth"})
    (is (= (board/get-status) "Welcome to Arkham Horror!\nPhase: Setup\nCommands:\n\t(def investigator (investigator/make <name> {:speed <speed>, :fight <fight>, :lore <lore>}))\n\t(awaken)"))
    (awaken)
    (is (= (board/get-status) "Azathoth has destroyed the world!\nPhase: Lost\nCommands:\n\t(board/reset)"))))

(deftest cthulu-test
  (testing "Devour once meters exhausted"
    (board/reset)
    (begin {:ancient-one "Cthulu"})
    (let [monterey-jack (investigator/make "Monterey Jack" {:speed 2 :fight 2 :lore 2})]
      (is (= (@monterey-jack :maximum-stamina) 6))
      (is (= (@monterey-jack :maximum-sanity)  2))
      (awaken)
      (is (= (board/get-status) (join "\n" ["Monterey Jack:"
                                              "\tStamina: 6/6"
                                              "\tSanity:  2/2"
                                              "\tSpeed:  1 <2> 3  4 "
                                              "\tSneak:  3 <2> 1  0 "
                                              "\tFight: <2> 3  4  5 "
                                              "\tWill:  <3> 2  1  0 "
                                              "\tLore:   1 <2> 3  4 "
                                              "\tLuck:   5 <4> 3  2 "
                                              "Phase: Upkeep"
                                              "Commands:"
                                              "\t(investigator/focus <investigator> {:speed-sneak <speed-delta>, :fight-will <fight-delta>, :lore-luck <lore-delta>})"])))
      (end-upkeep)
      (is (= (board/get-status) "Doom track: 13\nPhase: Attack\nCommands:\n\t(investigator/attack <investigator>)"))
      (investigator/attack monterey-jack)
      (is (= (board/get-status) "Roll: \nPhase: Attack\nCommands:\n\t(accept-roll)"))
      (accept-roll)
      (is (= (board/get-status) "Remaining meters:\n\tMonterey Jack\n\t\t:maximum-stamina: 6/6\n\t\t:maximum-sanity: 2/2\nPhase: Defend\nCommands:\n\t(investigator/defend <investigator> <meter>)"))
      (investigator/defend monterey-jack :maximum-stamina)
      (is (= (board/get-status) (join "\n" ["Monterey Jack:"
                                              "\tStamina: 5/5"
                                              "\tSanity:  2/2"
                                              "\tSpeed:  1 <2> 3  4 "
                                              "\tSneak:  3 <2> 1  0 "
                                              "\tFight: <2> 3  4  5 "
                                              "\tWill:  <3> 2  1  0 "
                                              "\tLore:   1 <2> 3  4 "
                                              "\tLuck:   5 <4> 3  2 "
                                              "Phase: Upkeep"
                                              "Commands:"
                                              "\t(investigator/focus <investigator> {:speed-sneak <speed-delta>, :fight-will <fight-delta>, :lore-luck <lore-delta>})"])))
      )))

(deftest monterey-jack-test
  (testing "Basic initialize"
    (board/reset)
    (begin {:ancient-one "Azathoth"})
    (let [investigator (investigator/make "Monterey Jack" {:speed 3 :fight 3 :lore 4})]
      (is (= (:name @investigator) "Monterey Jack"))
      (is (= (:speed @investigator) 3))
      (is (= (:sneak @investigator) 1))
      (is (= (:fight @investigator) 3))
      (is (= (:will  @investigator) 2))
      (is (= (:lore  @investigator) 4))
      (is (= (:luck  @investigator) 2))
      (is (= (:maximum-stamina @investigator) 7))
      (is (= (:maximum-sanity  @investigator) 3))))
  (testing "Cannot initialize after awakening"
    (board/reset)
    (begin {:ancient-one "Azathoth"})
    (awaken)
    (is (thrown? AssertionError (investigator/make "Monterey Jack" {:speed 2 :fight 2 :lore 2}))))
  (testing "Cannot initialize before beginning"
    (board/reset)
    (is (thrown? AssertionError (investigator/make "Monterey Jack" {:speed 2 :fight 2 :lore 2}))))
  (testing "Stat bounds"
    (board/reset)
    (begin {:ancient-one "Azathoth"})
    (is (thrown? AssertionError (investigator/make "Monterey Jack" {:speed 0 :fight 2 :lore 2})))
    (is (thrown? AssertionError (investigator/make "Monterey Jack" {:speed 5 :fight 2 :lore 2})))
    (is (thrown? AssertionError (investigator/make "Monterey Jack" {:speed 2 :fight 1 :lore 2})))
    (is (thrown? AssertionError (investigator/make "Monterey Jack" {:speed 2 :fight 6 :lore 2})))
    (is (thrown? AssertionError (investigator/make "Monterey Jack" {:speed 2 :fight 2 :lore 0})))
    (is (thrown? AssertionError (investigator/make "Monterey Jack" {:speed 2 :fight 2 :lore 5}))))
  (testing "Focus"
    (board/reset)
    (begin {:ancient-one "Cthulu"})
    (let [investigator (investigator/make "Monterey Jack" {:speed 2 :fight 2 :lore 2})]
      (awaken)
      (investigator/focus investigator {:speed-sneak 2})
      (is (= (@investigator :speed) 4))
      (is (= (@investigator :sneak) 0)))
    (board/reset)
    (begin {:ancient-one "Cthulu"})
    (let [investigator (investigator/make "Monterey Jack" {:speed 2 :fight 2 :lore 2})]
      (awaken)
      (investigator/focus investigator {:fight-will 1 :lore-luck 1})
      (is (= (@investigator :fight) 3))
      (is (= (@investigator :will)  2))
      (is (= (@investigator :lore)  3))
      (is (= (@investigator :luck)  3))
      (is (thrown? AssertionError (investigator/focus investigator {:fight-will 1}))))
    (board/reset)
    (begin {:ancient-one "Cthulu"})
    (let [investigator (investigator/make "Monterey Jack" {:speed 2 :fight 2 :lore 2})]
      (awaken)
      (is (thrown? AssertionError (investigator/focus investigator {:speed-sneak 1
                                                                    :fight-will 1
                                                                    :lore-luck -1}))))
    ;; TODO: post-refresh refocusing
    ))
