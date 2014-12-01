(ns arkham-horror.setup-test
  (:require [clojure.test :refer :all]
            [arkham-horror.setup :as setup]
            [arkham-horror.ancient-one :as ancient-one]
            [arkham-horror.game :as game]
            [arkham-horror.investigator :as investigator]
            [arkham-horror.combat :as combat]
            [arkham-horror.dice :as dice]
            [arkham-horror.stat :as stat]))

(def active-game (setup/begin :cthulu ["Monterey Jack"]))
(deftest begin-test
  (is (= (:investigators active-game) [(investigator/make "Monterey Jack")])))

(def init-game (setup/init active-game [{:speed 2 :fight 2 :lore 2}]))
(deftest init-test
  (is (= (:investigators init-game)
         [(investigator/init (investigator/make "Monterey Jack")
                             {:speed 2 :fight 2 :lore 2})])))

(def awakened-game (setup/awaken (setup/begin :cthulu ["Monterey Jack"])))
(deftest awaken-test
  (is (ancient-one/awakened? awakened-game)))

(def rigged-game (merge awakened-game
                        {:dice (dice/loaded 6)
                         :investigators (map #(stat/rig-fight % 19)
                                             (awakened-game :investigators))}))
(def focused-game (setup/focus awakened-game [{:fight-will 2}]))
(deftest attack-test
  (is (= (:investigators (setup/attack awakened-game))
         (:investigators (combat/ancient-one-attack awakened-game))))
  (is (= (:doom-track (setup/attack rigged-game)) 0))
  (is (= (map :focus (:investigators (setup/attack focused-game))) [2])))

(deftest focus-test
  (is (= (map stat/fight (:investigators focused-game)) [4])))

(deftest game-status-test
  (is (= (setup/game-status active-game) "Initialize investigators"))
  (is (= (setup/game-status init-game) "Awaken ancient one"))
  (is (= (setup/game-status awakened-game) "Attack\nDoom track: 13\nStats: (7 3)"))
  (is (= (setup/game-status (ancient-one/awaken (setup/begin :azathoth []))) "You lose"))
  (is (= (setup/game-status (setup/attack rigged-game)) "You win")))
