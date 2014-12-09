(ns arkham-horror.cthulu-test
  (:require [clojure.test :refer :all]
            [arkham-horror.game :as game]
            [arkham-horror.ancient-one :as ancient-one]
            [arkham-horror.investigator :as investigator]
            [arkham-horror.ancient-one.doom-track :as doom-track]
            [arkham-horror.combat :as combat]
            [arkham-horror.stat :as stat]
            [arkham-horror.investigator.dice :as dice]
            [arkham-horror.structure :as structure]))

(defn make-investigator-with [max-sanity max-stamina]
  (merge (investigator/make "Monterey Jack")
         {:maximum-sanity max-sanity
          :maximum-stamina max-stamina}))

(defn alter-investigator [game max-sanity max-stamina]
  (update-in game [:investigators]
             (fn [investigators]
               (map #(merge % {:maximum-sanity max-sanity
                               :maximum-stamina max-stamina})
                    investigators))))

(def awakened-game (ancient-one/awaken (game/make {:ancient-one :cthulu
                                                   :investigators ["Monterey Jack"]})))

(defn lost-after-attacks? [victim attacks]
  (if (zero? attacks)
    (game/lost? victim)
    (lost-after-attacks? (combat/ancient-one-attack victim) (dec attacks))))

(def base-game (game/make {:ancient-one :cthulu}))
(def awakened-game (alter-investigator awakened-game 1 1))
(def death-bed-game (alter-investigator awakened-game 1 1))
(def has-two-stamina-game (alter-investigator awakened-game 1 2))
(def has-two-sanity-game (alter-investigator awakened-game 2 1))
(def has-a-tougher-investigator (alter-investigator awakened-game 1 2))

(deftest ends-world-test
  (is (not (lost-after-attacks? awakened-game 0)))
  (is (lost-after-attacks? death-bed-game 1))
  (is (not (lost-after-attacks? has-two-stamina-game 1)))
  (is (lost-after-attacks? has-two-stamina-game 2))
  (is (not (lost-after-attacks? has-two-stamina-game 1)))
  (is (lost-after-attacks? has-two-stamina-game 2))
  (is (not (lost-after-attacks? has-two-stamina-game 1)))
  (is (lost-after-attacks? has-two-stamina-game 2))
  (is (not (lost-after-attacks? has-a-tougher-investigator 1)))
  (is (lost-after-attacks? has-a-tougher-investigator 2)))

(deftest doom-track-test
  (is (= (doom-track/capacity (doom-track/get (ancient-one/make :cthulu))) 13))
  (is (= (doom-track/level (structure/get-path awakened-game [ancient-one doom-track])) 13))
  (is (= (-> awakened-game
             (ancient-one/update #(doom-track/update % doom-track/retract))
             combat/ancient-one-attack
             (structure/get-path [ancient-one doom-track])
             doom-track/level)
         13)))
