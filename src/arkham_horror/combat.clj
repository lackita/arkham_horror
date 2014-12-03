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

(defn start-attack [game]
  (assoc-in game [:combat :current-attacker] 0))

(defn end-attack [game]
  (dissoc game :combat))

(defn in-combat? [game]
  (game :combat))

(defn current-attacker [game]
  (let [position (-> game :combat :current-attacker)
        investigators (game :investigators)]
    (when (and position (< position (count investigators)))
      (nth investigators position))))

(defn accept-roll [game]
  game)

(defn investigator-attack [game]
  (update-in (->> (current-attacker game)
                  (combat-check-rolls game)
                  (dice/combat-check game)
                  (filter #{5 6})
                  count
                  (apply-successes game))
             [:combat :current-attacker]
             inc))
