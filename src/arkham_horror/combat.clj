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

(defn apply-successes [game successes]
  (loop [game game
         successes (+ successes (or (game :remaining-successes) 0))]
    (if (or (zero? (count (game :investigators)))
            (< successes (count (game :investigators))))
      (assoc game :remaining-successes successes)
      (recur (doom-track/retract game)
             (- successes (count (game :investigators)))))))

(defn investigator-attack [game investigator]
  (->> investigator
       (combat-check-rolls game)
       (dice/combat-check game)
       (filter #{5 6})
       count
       (apply-successes game)))

(defn investigators-attack [game]
  (loop [game game
         investigators (game :investigators)]
    (if (empty? investigators)
      game
      (recur (investigator-attack game (first investigators))
             (rest investigators)))))
