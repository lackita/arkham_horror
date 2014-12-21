(ns arkham-horror.acceptance-test
  (:require [clojure.test :refer :all]
            [clojure.string :refer [join]]
            [arkham-horror.core :refer :all]
            [arkham-horror.board :as board]
            [arkham-horror.investigator :as investigator]))

(deftest azathoth-test
  (testing "Awakening ends world"
    (let [board (board/make)]
      (is (= (board/get-status board)
             (board/make-status "Game not started."
                                "None"
                                '(begin <config>))))
      (begin board {:ancient-one "Azathoth"})
      (is (= (board/get-status board)
             (board/make-status "Welcome to Arkham Horror!"
                                "Setup"
                                '(def investigator (investigator/make <name> {:speed <speed>
                                                                              :fight <fight>
                                                                              :lore <lore>}))
                                '(awaken))))
      (awaken board)
      (is (= (board/get-status board)
             (board/make-status "Azathoth has destroyed the world!"
                                "Lost"
                                '(board/reset)))))))

(deftest cthulu-test
  (testing "Devour once meters exhausted"
    (let [board (board/make)]
      (begin board {:ancient-one "Cthulu"})
      (let [monterey-jack (investigator/make board "Monterey Jack" {:speed 2 :fight 2 :lore 2})]
        (is (= (@monterey-jack :maximum-stamina) 6))
        (is (= (@monterey-jack :maximum-sanity)  2))
        (awaken board)
        (is (= (board/get-status board) (join "\n" ["Monterey Jack:"
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
        (end-upkeep board)
        (is (= (board/get-status board) "Doom track: 13\nPhase: Attack\nCommands:\n\t(investigator/attack <investigator>)"))
        (investigator/attack monterey-jack)
        (is (= (board/get-status board) "Roll: \nPhase: Attack\nCommands:\n\t(accept-roll)"))
        (accept-roll board)
        (is (= (board/get-status board) "Remaining meters:\n\tMonterey Jack\n\t\t:maximum-stamina: 6/6\n\t\t:maximum-sanity: 2/2\nPhase: Defend\nCommands:\n\t(investigator/defend <investigator> <meter>)"))
        (investigator/defend monterey-jack :maximum-stamina)
        (is (= (board/get-status board) (join "\n" ["Monterey Jack:"
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
        ))))

(deftest monterey-jack-test
  (testing "Basic initialize"
    (let [board (board/make)]
      (begin board {:ancient-one "Azathoth"})
      (let [investigator (investigator/make board "Monterey Jack" {:speed 3 :fight 3 :lore 4})]
        (is (= (:name @investigator) "Monterey Jack"))
        (is (= (:speed @investigator) 3))
        (is (= (:sneak @investigator) 1))
        (is (= (:fight @investigator) 3))
        (is (= (:will  @investigator) 2))
        (is (= (:lore  @investigator) 4))
        (is (= (:luck  @investigator) 2))
        (is (= (:maximum-stamina @investigator) 7))
        (is (= (:maximum-sanity  @investigator) 3)))))
  (testing "Cannot initialize after awakening"
    (let [board (board/make)]
      (begin board {:ancient-one "Azathoth"})
      (awaken board)
      (is (thrown? AssertionError (investigator/make board
                                                     "Monterey Jack"
                                                     {:speed 2 :fight 2 :lore 2})))))
  (testing "Cannot initialize before beginning"
    (is (thrown? AssertionError (investigator/make (board/make)
                                                   "Monterey Jack"
                                                   {:speed 2 :fight 2 :lore 2}))))
  (testing "Stat bounds"
    (let [board (board/make)]
      (begin board {:ancient-one "Azathoth"})
      (is (thrown? AssertionError (investigator/make board
                                                     "Monterey Jack"
                                                     {:speed 0 :fight 2 :lore 2})))
      (is (thrown? AssertionError (investigator/make board
                                                     "Monterey Jack"
                                                     {:speed 5 :fight 2 :lore 2})))
      (is (thrown? AssertionError (investigator/make board
                                                     "Monterey Jack"
                                                     {:speed 2 :fight 1 :lore 2})))
      (is (thrown? AssertionError (investigator/make board
                                                     "Monterey Jack"
                                                     {:speed 2 :fight 6 :lore 2})))
      (is (thrown? AssertionError (investigator/make board
                                                     "Monterey Jack"
                                                     {:speed 2 :fight 2 :lore 0})))
      (is (thrown? AssertionError (investigator/make board
                                                     "Monterey Jack"
                                                     {:speed 2 :fight 2 :lore 5})))))
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
