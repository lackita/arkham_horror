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
              :investigators (map #(investigator/init (investigator/make {:fight %})
                                                      {:speed 0 :fight % :lore 0}) fights)}))

(defn attack-and-get-level [game]
  (-> game
      ancient-one/awaken
      combat/investigators-attack
      doom-track/level))

(deftest attack-test
  (is (= (attack-and-get-level (make-game-with [7] 6)) 12))
  (is (= (attack-and-get-level (make-game-with [8] 6)) 11))
  (is (= (attack-and-get-level (make-game-with [7 7] 6)) 12))
  (is (= (attack-and-get-level (make-game-with [7] 1)) 13)))
