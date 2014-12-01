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

(defn combat-check-rolls [game fighter]
  (apply +
   (stat/fight fighter)
   (ancient-one/combat-modifier game)
   (map :combat-modifier (investigator/items fighter))))

(defn investigators-attack
  ([game]
     (->> (game :investigators)
          (map #(dice/combat-check game (combat-check-rolls game %)))
          (apply +)
          (investigators-attack game)))
  ([game successes]
     (first (drop (quot successes (count (game :investigators)))
                  (iterate doom-track/retract game)))))
