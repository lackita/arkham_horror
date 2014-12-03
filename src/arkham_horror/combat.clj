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

(defn start-attack [game]
  (assoc game
    :combat {:current-attacker 0
             :successes 0}))

(defn end-attack [game]
  (dissoc game :combat))

(defn in-combat? [game]
  (game :combat))

(defn current-attacker [game]
  (let [position (-> game :combat :current-attacker)
        investigators (game :investigators)]
    (when (and position (< position (count investigators)))
      (nth investigators position))))

(defn count-successes [game]
  (+ (count (filter #{5 6} (-> game :combat :roll)))
     (or (-> game :combat :remainder) 0)))

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
    (assoc game
      :combat (dissoc (update-in combat [:current-attacker] inc) :roll))))

(defn pending-roll [game]
  (-> game :combat :roll))

(defn investigator-attack [game]
  (assoc-in game [:combat :roll] (->> (current-attacker game)
                                      (combat-check-rolls game)
                                      (dice/combat-check game))))

(defn bullwhip [game]
  (if (-> game :combat :bullwhip)
    game
    (assoc-in (update-in game [:combat :roll]
                         #(conj (rest (sort %))
                                ((game :dice))))
              [:combat :bullwhip]
              true)))
