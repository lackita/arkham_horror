(ns arkham-horror.combat
  (:require [arkham-horror.investigator :as investigator]
            [arkham-horror.ancient-one.doom-track :as doom-track]
            [arkham-horror.stat :as stat]
            [arkham-horror.investigator.dice :as dice]
            [arkham-horror.investigator.items :as items]
            [arkham-horror.ancient-one :as ancient-one]
            [arkham-horror.phase :as phase]
            [arkham-horror.structure :as structure]))

(defn ancient-one-attack [game]
  (structure/update-path (assoc game :investigators
                                (map investigator/reduce-max-sanity-or-stamina
                                     (game :investigators)))
                         [ancient-one doom-track]
                         doom-track/advance))

(defn start-attack [game]
  (merge (phase/start game)
         {:combat {:successes 0
                   :remainder 0}}))

(defn end-attack [game]
  (dissoc (phase/end game) :combat))

(defn in-combat? [game]
  (game :combat))

(defn count-successes [game]
  (+ (count (filter #{5 6} (dice/pending-roll
                            (structure/get-path game [phase investigator dice]))))
     (or (-> game :combat :remainder) 0)))

(defn apply-successes
  ([game] (apply-successes game (count-successes game)))
  ([game successes]
     (if (or (nil? (structure/get-path game [phase investigator]))
             (< successes (count (phase/all-investigators (phase/get game)))))
       (assoc-in game [:combat :remainder] successes)
       (apply-successes (structure/update-path game [ancient-one doom-track] doom-track/retract)
                        (- successes (count (phase/all-investigators (phase/get game))))))))

(defn accept-roll [game]
  (phase/update (structure/update-path (apply-successes game)
                                       [phase investigator dice]
                                       dice/accept-roll)
                phase/advance))

(defn combat-check-rolls [game fighter]
  (apply + (stat/fight fighter)
         (ancient-one/combat-modifier game)
         (map :combat-modifier (items/get fighter))))

(defn investigator-attack [game]
  {:pre [(:dice (structure/get-path game [phase investigator]))]}
  (structure/update-path game [phase investigator dice]
                         #(dice/combat-check % investigator
                           (apply + (ancient-one/combat-modifier (ancient-one/get game))
                                  (map :combat-modifier
                                       (structure/get-path game [phase investigator items]))))))
