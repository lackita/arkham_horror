(ns arkham-horror.game-test
  (:require [clojure.test :refer :all]
            [arkham-horror.ancient-one :as ancient-one]
            [arkham-horror.game :as game]
            [arkham-horror.investigator :as investigator]
            [arkham-horror.phase :as phase]
            [arkham-horror.help :as help]))

(def cthulu-game (game/make {:ancient-one "Cthulu"
                             :investigators ["Monterey Jack"]}))
(def azathoth-game (game/make {:ancient-one "Azathoth"
                               :investigators ["Monterey Jack"]}))
(def awakened-game (ancient-one/awaken cthulu-game))
(def won-game (ancient-one/update awakened-game ancient-one/defeat))
(def lost-game (ancient-one/awaken azathoth-game))

(defn init-after-one-round [game]
  (game/advance-phase
   (game/init-investigator
    (phase/start-init game)
    {:speed 2 :fight 2 :lore 2})))

(def single-investigator cthulu-game)
(def two-investigators (game/make {:ancient-one "Cthulu"
                                   :investigators (repeat 2 "Monterey Jack")}))
(def initialized-investigator (phase/end-init
                               (game/init-investigator
                                (phase/start-init single-investigator)
                                {:speed 2 :fight 2 :lore 2})))
(deftest help-test
  (is (= (help/get-available-actions (phase/start-upkeep awakened-game))
         '[(focus-investigator {:speed-sneak <delta> :fight-will <delta> :lore-luck <delta>})
           (advance-phase)])))

(deftest won-test
  (is (game/won? won-game))
  (is (not (game/won? awakened-game)))
  (is (not (game/won? cthulu-game))))

(deftest over-test
  (is (game/over? lost-game))
  (is (not (game/over? azathoth-game)))
  (is (game/over? won-game)))

(deftest message-test
  (is (= (game/message awakened-game) "Cthulu awakened"))
  (is (= (game/message lost-game) "Azathoth has destroyed the world!"))
  (is (= (game/message won-game) "You win")))

(deftest init-investigator-test
  (is (= (phase/get (game/init-investigator (phase/start-init cthulu-game)
                                            {:speed 2 :fight 3 :lore 4}))
         (phase/init-investigator (phase/make [(investigator/make "Monterey Jack")])
                                  {:speed 2 :fight 3 :lore 4}))))

(deftest advance-phase-test
  (is (= (phase/get (game/advance-phase (phase/start-init cthulu-game)))
         (phase/advance (phase/get (phase/start-init cthulu-game))))))

(deftest focus-investigator-test
  (is (= (phase/get (game/focus-investigator (phase/start-init initialized-investigator)
                                             {:fight-will 2}))
         (phase/focus-investigator (phase/get (phase/start-init initialized-investigator))
                                   {:fight-will 2}))))
