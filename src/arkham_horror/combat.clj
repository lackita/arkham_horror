(ns arkham-horror.combat
  (:refer-clojure :exclude [get])
  (:require [arkham-horror.investigator :as investigator]
            [arkham-horror.ancient-one.doom-track :as doom-track]
            [arkham-horror.stat :as stat]
            [arkham-horror.investigator.dice :as dice]
            [arkham-horror.investigator.items :as items]
            [arkham-horror.ancient-one :as ancient-one]
            [arkham-horror.phase :as phase]
            [arkham-horror.structure :as structure]))

(defn update [game function]
  (update-in game [:combat] function))

(defn get [game]
  (game :combat))

(defn make []
  {:successes 0
   :remainder 0})

(defn start [game]
  (assoc (phase/start game) :combat (make)))

(defn end [game]
  (dissoc (phase/end game) :combat))

(defn ancient-one-attack [game]
  (structure/update-path (investigator/update-all game investigator/reduce-max-sanity-or-stamina)
                         [ancient-one doom-track]
                         doom-track/advance))

(defn successes [game]
  (->> (structure/get-path game [phase investigator dice])
       dice/pending-roll
       (filter #{5 6})
       count))

(defn count-successes [game]
  (+ (successes game)
     (or (:remainder (get game)) 0)))

(defn count-investigators [game]
  (count (phase/all-investigators (phase/get game))))

(defn save-remainder [game remainder]
  (update game #(assoc % :remainder remainder)))

(defn apply-successes [game successes]
  (if (or (phase/over? (phase/get game)) (< successes (count-investigators game)))
    (save-remainder game successes)
    (apply-successes (structure/update-path game [ancient-one doom-track] doom-track/retract)
                     (- successes (count-investigators game)))))

(defn accept-roll [game]
  (-> (apply-successes game (count-successes game))
      (structure/update-path [phase investigator dice] dice/accept-roll)
      (phase/update phase/advance)))

(defn calculate-combat-modifier [ancient-one items]
  (apply + (ancient-one/combat-modifier ancient-one)
         (map :combat-modifier items)))

(defn investigator-attack [game]
  {:pre [(:dice (structure/get-path game [phase investigator]))]}
  (structure/update-path game [phase investigator dice]
                         #(->> (items/get investigator)
                               (calculate-combat-modifier (ancient-one/get game))
                               (dice/combat-check % investigator))))
