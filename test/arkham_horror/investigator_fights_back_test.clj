(ns arkham-horror.investigator-fights-back-test
  (:require [clojure.test :refer :all]
            [arkham-horror.ancient-one :as ancient-one]
            [arkham-horror.investigators :as investigators]
            [arkham-horror.investigator :as investigator]
            [arkham-horror.game :as game]
            [arkham-horror.dice :as dice]
            [arkham-horror.doom-track :as doom-track]
            [arkham-horror.combat :as combat]))

(defn make-game-with [fights pips]
  (game/make {:ancient-one :cthulu
              :dice (dice/loaded pips)
              :investigators (map #(investigator/init (investigator/make "Monterey Jack")
                                                      {:speed 0 :fight % :lore 0}) fights)}))

(defn everybody-attack [game]
  (if (combat/current-attacker game)
    (everybody-attack (combat/accept-roll (combat/investigator-attack game)))
    game))

(defn attack-and-get-level [game]
  (-> game
      ancient-one/awaken
      combat/start-attack
      everybody-attack
      doom-track/level))

(deftest attack-test
  (is (= (attack-and-get-level (make-game-with [3] 6)) 12))
  (is (= (attack-and-get-level (make-game-with [4] 6)) 11))
  (is (= (attack-and-get-level (make-game-with [3 3] 6)) 12))
  (is (= (attack-and-get-level (make-game-with [4] 1)) 13)))
