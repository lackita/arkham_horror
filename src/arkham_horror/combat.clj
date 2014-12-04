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

(defn combat-check-rolls [game fighter]
  (apply +
         (stat/fight fighter)
         (ancient-one/combat-modifier game)
         (map :combat-modifier (investigator/items fighter))))

(defn start-attack [game]
  (merge (phase/start game)
         {:combat {:successes 0
                   :bullwhip 0}}))

(defn end-attack [game]
  (dissoc (phase/end game) :combat))

(defn in-combat? [game]
  (game :combat))

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
    (assoc (phase/advance game)
      :combat (dissoc combat :roll))))

(defn pending-roll [game]
  (-> game :combat :roll))

(defn investigator-attack [game]
  (let [roll (->> (phase/investigator game)
                  (combat-check-rolls game)
                  (dice/combat-check game))]
    (dice/save-roll (assoc-in game [:combat :roll] roll)
                    roll)))

(defn bullwhip [game]
  (if (< (-> game :combat :bullwhip)
         (count (->> game :investigators
                     (mapcat :items)
                     (filter #(= "Bullwhip"
                                 (:name %))))))
    (update-in (update-in game [:combat :roll]
                          #(conj (rest (sort %))
                                 (dice/roll (game :dice))))
               [:combat :bullwhip]
               inc)
    game))
