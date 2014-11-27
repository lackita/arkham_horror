(ns arkham-horror.combat
  (:require [arkham-horror.investigator :as investigator]
            [arkham-horror.investigators :as investigators]
            [arkham-horror.doom-track :as doom-track]
            [arkham-horror.stat :as stat]
            [arkham-horror.dice :as dice]))

(defn ancient-one-attack [game]
  (-> game
      investigators/resolve-ancient-one-attack
      doom-track/advance))

(defn investigators-attack
  ([game]
     (investigators-attack game (apply + (map #(dice/combat-check game (stat/fight %))
                                              (game :investigators)))))
  ([game successes]
     (first (drop successes (iterate doom-track/retract game)))))
