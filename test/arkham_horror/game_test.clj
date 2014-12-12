(ns arkham-horror.game-test
  (:require [clojure.test :refer :all]
            [arkham-horror.setup :as setup]
            [arkham-horror.ancient-one :as ancient-one]
            [arkham-horror.game :as game]
            [arkham-horror.investigator :as investigator]
            [arkham-horror.stat :as stat]
            [arkham-horror.combat :as combat]
            [arkham-horror.phase :as phase]))

(def cthulu-game (game/make {:ancient-one "Cthulu"
                             :investigators ["Monterey Jack"]}))
(def azathoth-game (game/make {:ancient-one "Azathoth"
                               :investigators ["Monterey Jack"]}))
(def init-game (setup/init cthulu-game [{:speed 2 :fight 2 :lore 2}]))
(def awakened-game (ancient-one/awaken cthulu-game))
(def won-game (ancient-one/update awakened-game ancient-one/defeat))
(def lost-game (ancient-one/awaken azathoth-game))
(def attack-started-game (combat/start awakened-game))
(def rigged-game (combat/start
                  (ancient-one/awaken
                   (setup/init
                    (game/make {:ancient-one "Cthulu"
                                :investigators ["Monterey Jack"]
                                :dice 6})
                    [{:speed 0 :fight 19 :lore 0}]))))

(deftest won-test
  (is (game/won? won-game))
  (is (not (game/won? awakened-game)))
  (is (not (game/won? cthulu-game))))

(deftest over-test
  (is (game/over? lost-game))
  (is (not (game/over? azathoth-game)))
  (is (game/over? won-game)))

(deftest message-test
  (is (= (game/message cthulu-game) "Initialize investigators"))
  (is (= (game/message init-game) "Awaken ancient one"))
  (is (= (game/message awakened-game) "Refresh investigators"))
  (is (= (game/message attack-started-game) "Attack\nDoom track: 13"))
  (is (= (game/message (combat/investigator-attack rigged-game))
         "Roll: 6 6 6 6 6 6 6 6 6 6 6 6 6 6 6 6 6"))
  (is (= (game/message (combat/accept-roll
                        (combat/investigator-attack attack-started-game))) "Defend"))
  (is (= (game/message (ancient-one/awaken azathoth-game)) "You lose"))
  (is (= (game/message (combat/accept-roll
                        (combat/investigator-attack rigged-game))) "You win"))
  )

(deftest init-investigator-test
  (is (= (phase/get (game/init-investigator (phase/start cthulu-game)
                                            {:speed 2 :fight 3 :lore 4}))
         (phase/init-investigator (phase/make [(investigator/make "Monterey Jack")])
                                  {:speed 2 :fight 3 :lore 4}))))

(deftest advance-phase-test
  (is (= (phase/get (game/advance-phase (phase/start cthulu-game)))
         (phase/advance (phase/get (phase/start cthulu-game))))))
