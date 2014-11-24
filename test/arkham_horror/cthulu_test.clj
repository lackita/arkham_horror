(ns arkham-horror.cthulu-test
  (:require [clojure.test :refer :all]
            [arkham-horror.game :as game]
            [arkham-horror.ancient-one :as ancient-one]
            [arkham-horror.player :as player]
            [arkham-horror.doom-track :as doom-track]))

(defn make-player-with [max-sanity max-stamina]
  (player/make {:maximum-sanity max-sanity
                :maximum-stamina max-stamina}))

(defn make-awakened-game [config]
  (ancient-one/awaken (game/make (merge {:ancient-one :cthulu}
                                        config))))

(defn lost-after-attacks? [victim attacks]
  (if (zero? attacks)
    (game/lost? victim)
    (lost-after-attacks? (ancient-one/attack victim) (dec attacks))))

(def base-game (game/make {:ancient-one :cthulu}))
(def awakened-game (make-awakened-game {:players [(make-player-with 1 1)]}))
(def death-bed-game (make-awakened-game {:players [(make-player-with 1 1)]}))
(def has-two-stamina-game (make-awakened-game {:players [(make-player-with 1 2)]}))
(def has-two-sanity-game (make-awakened-game {:players [(make-player-with 2 1)]}))
(def has-a-tougher-player (make-awakened-game {:players [(make-player-with 1 1)
                                                         (make-player-with 1 2)]}))

(deftest ends-world-test
  (is (not (lost-after-attacks? awakened-game 0)))
  (is (lost-after-attacks? death-bed-game 1))
  (is (not (lost-after-attacks? has-two-stamina-game 1)))
  (is (lost-after-attacks? has-two-stamina-game 2))
  (is (not (lost-after-attacks? has-two-stamina-game 1)))
  (is (lost-after-attacks? has-two-stamina-game 2))
  (is (not (lost-after-attacks? has-two-stamina-game 1)))
  (is (lost-after-attacks? has-two-stamina-game 2))
  (is (not (lost-after-attacks? has-a-tougher-player 1)))
  (is (lost-after-attacks? has-a-tougher-player 2)))

(deftest doom-track-test
  (is (= (doom-track/capacity base-game) 13))
  (is (= (doom-track/current-level base-game) 0))
  (is (= (doom-track/current-level (doom-track/advance base-game)) 1))
  (is (= (doom-track/current-level awakened-game) 13))
  (is (= (->> base-game
              (iterate doom-track/advance)
              (drop 14)
              first
              doom-track/current-level)
         13))
  (is (= (-> base-game
             doom-track/advance
             doom-track/retract
             doom-track/current-level)
         0))
  (is (= (-> base-game
             doom-track/retract
             doom-track/current-level)
         0))
  (is (= (-> awakened-game
             doom-track/retract
             ancient-one/attack
             doom-track/current-level)
         13)))
