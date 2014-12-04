(ns arkham-horror.combat
  (:require [arkham-horror.investigator :as investigator]
            [arkham-horror.investigators :as investigators]
            [arkham-horror.doom-track :as doom-track]
            [arkham-horror.stat :as stat]
            [arkham-horror.dice :as dice]
            [arkham-horror.ancient-one :as ancient-one]
            [arkham-horror.phase :as phase]))

(defn ancient-one-attack [game]
  (-> game
      investigators/resolve-ancient-one-attack
      doom-track/advance))

(defn start-attack [game]
  (merge (phase/start game)
         {:combat {:successes 0
                   :remainder 0
                   :bullwhip 0}}))

(defn end-attack [game]
  (dissoc (phase/end game) :combat))

(defn in-combat? [game]
  (game :combat))

(defn count-successes [game]
  (+ (count (filter #{5 6} (dice/pending-roll (dice/get game))))
     (-> game :combat :remainder)))

(defn apply-successes
  ([game] (apply-successes game (count-successes game)))
  ([game successes]
     (if (or (zero? (count (game :investigators)))
             (< successes (count (game :investigators))))
       (assoc-in game [:combat :remainder] successes)
       (apply-successes (doom-track/retract game)
                        (- successes (count (game :investigators)))))))

(defn accept-roll [game]
  (let [game (apply-successes game)
        combat (game :combat)]
    (dice/accept-roll (phase/advance game))))

(defn combat-check-rolls [game fighter]
  (apply +
         (stat/fight fighter)
         (ancient-one/combat-modifier game)
         (map :combat-modifier (investigator/items fighter))))

(defn investigator-attack [game]
  (dice/set game (dice/get (dice/combat-check game (phase/investigator game)))))

(defn bullwhip [game]
  (if (< (-> game :combat :bullwhip)
         (count (->> (phase/investigator game) :items
                     (filter #(= "Bullwhip" (:name %))))))
    (update-in (dice/update game dice/reroll-lowest) [:combat :bullwhip] inc)
    game))
