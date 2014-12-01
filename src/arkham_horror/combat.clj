(ns arkham-horror.combat
  (:require [arkham-horror.investigator :as investigator]
            [arkham-horror.investigators :as investigators]
            [arkham-horror.doom-track :as doom-track]
            [arkham-horror.stat :as stat]
            [arkham-horror.dice :as dice]
            [arkham-horror.ancient-one :as ancient-one]))

(defn ancient-one-attack [game]
  (-> game
      investigators/resolve-ancient-one-attack
      doom-track/advance))

(defn investigators-attack
  ([game]
     (investigators-attack game (apply + (map #(dice/combat-check game
                                                                  (+ (stat/fight %)
                                                                     (ancient-one/combat-modifier game)
                                                                     4))
                                              (game :investigators)))))
  ([game successes]
     (first (drop (quot successes (count (game :investigators)))
                  (iterate doom-track/retract game)))))
