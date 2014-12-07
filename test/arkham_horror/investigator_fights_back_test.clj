(ns arkham-horror.investigator-fights-back-test
  (:require [clojure.test :refer :all]
            [arkham-horror.ancient-one :as ancient-one]
            [arkham-horror.investigator :as investigator]
            [arkham-horror.game :as game]
            [arkham-horror.setup :as setup]
            [arkham-horror.phase :as phase]
            [arkham-horror.investigator.dice :as dice]
            [arkham-horror.ancient-one.doom-track :as doom-track]
            [arkham-horror.combat :as combat]))

(defn make-game-with [fights pips]
  (setup/init (game/make {:ancient-one :cthulu
                          :dice pips
                          :investigators [(repeat "Monterey Jack" (count fights))]})
              (map (fn [fight] {:speed 0 :fight fight :lore 0}) fights)))

(defn everybody-attack [game]
  (if (phase/investigator game)
    (everybody-attack (combat/accept-roll (combat/investigator-attack game)))
    game))

(defn attack-and-get-level [game]
  (-> game
      ancient-one/awaken
      combat/start-attack
      everybody-attack
      combat/end-attack
      ancient-one/get
      doom-track/get
      doom-track/level))

(deftest attack-test
  (is (= (attack-and-get-level (make-game-with [3] 6)) 12))
  (is (= (attack-and-get-level (make-game-with [4] 6)) 11))
  (is (= (attack-and-get-level (make-game-with [3 3] 6)) 12))
  (is (= (attack-and-get-level (make-game-with [4] 1)) 13)))
