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
  (is (= (help/get-message single-investigator) "Welcome to Arkham Horror!"))
  (is (= (help/get-available-actions single-investigator) '(start-init)))
  (is (= (help/get-available-actions (phase/start-init single-investigator))
         '(init-investigator {:speed <speed> :fight <fight> :lore <lore>})))
  (is (= (help/get-available-actions (game/init-investigator
                                      (phase/start-init single-investigator)
                                      {:speed 2 :fight 2 :lore 2}))
         '(advance-phase)))
  (is (= (help/get-available-actions (init-after-one-round two-investigators))
         '(init-investigator {:speed <speed> :fight <fight> :lore <lore>})))
  (is (= (help/get-available-actions (init-after-one-round single-investigator))
         '(end-init)))
  (is (= (help/get-available-actions initialized-investigator)
         '(awaken)))
  (is (= (help/get-available-actions awakened-game)
         '(start-upkeep))))

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
  (is (= (game/message lost-game) "You lose"))
  (is (= (game/message won-game) "You win")))

(deftest init-investigator-test
  (is (= (phase/get (game/init-investigator (phase/start cthulu-game)
                                            {:speed 2 :fight 3 :lore 4}))
         (phase/init-investigator (phase/make [(investigator/make "Monterey Jack")])
                                  {:speed 2 :fight 3 :lore 4}))))

(deftest advance-phase-test
  (is (= (phase/get (game/advance-phase (phase/start cthulu-game)))
         (phase/advance (phase/get (phase/start cthulu-game))))))

(deftest focus-investigator-test
  (is (= (phase/get (game/focus-investigator (phase/start initialized-investigator)
                                             {:fight-will 2}))
         (phase/focus-investigator (phase/get (phase/start initialized-investigator))
                                   {:fight-will 2}))))
