(ns arkham-horror.combat
  (:require [arkham-horror.investigator :as investigator]
            [arkham-horror.ancient-one.doom-track :as doom-track]
            [arkham-horror.stat :as stat]
            [arkham-horror.investigator.dice :as dice]
            [arkham-horror.investigator.items :as items]
            [arkham-horror.ancient-one :as ancient-one]
            [arkham-horror.phase :as phase]))

(defn ancient-one-attack [game]
  (ancient-one/update (assoc game :investigators
                             (map investigator/reduce-max-sanity-or-stamina
                                  (game :investigators)))
                      #(doom-track/update % doom-track/advance)))

(defn start-attack [game]
  (merge (phase/start game)
         {:combat {:successes 0
                   :remainder 0}}))

(defn end-attack [game]
  (dissoc (phase/end game) :combat))

(defn in-combat? [game]
  (game :combat))

(defn count-successes [game]
  (+ (count (filter #{5 6} (dice/pending-roll (dice/get
                                               (investigator/get (phase/get game))))))
     (or (-> game :combat :remainder) 0)))

(defn apply-successes
  ([game] (apply-successes game (count-successes game)))
  ([game successes]
     (if (or (nil? (investigator/get (phase/get game)))
             (< successes (count (phase/all-investigators (phase/get game)))))
       (assoc-in game [:combat :remainder] successes)
       (apply-successes (ancient-one/update game (fn [investigator]
                                                   (doom-track/update investigator
                                                                      doom-track/retract)))
                        (- successes (count (phase/all-investigators (phase/get game))))))))

(defn accept-roll [game]
  (phase/update (phase/update (apply-successes game)
                              #(update-in % [:current-investigator]
                                          (fn [investigator]
                                            (dice/update investigator
                                                         dice/accept-roll))))
                phase/advance))

(defn combat-check-rolls [game fighter]
  (apply + (stat/fight fighter)
         (ancient-one/combat-modifier game)
         (map :combat-modifier (items/get fighter))))

(defn investigator-attack [game]
  {:pre [(:dice (investigator/get (phase/get game)))]}
  (phase/update game (fn [phase]
                       (update-in phase [:current-investigator]
                                  (fn [investigator]
                                    (dice/update investigator
                                                 #(dice/combat-check
                                                   %
                                                   (investigator/get (phase/get game))
                                                   (apply + (ancient-one/combat-modifier (ancient-one/get game))
                                                          (map :combat-modifier
                                                               (items/get (investigator/get (phase/get game))))))))))))

(defn bullwhip [game]
  (if (> (count (items/unexhausted-named (items/get (investigator/get (phase/get game)))
                                         "Bullwhip"))
         0)
    (phase/update game (fn [phase]
                         (investigator/update
                          phase
                          (fn [investigator]
                            (items/update (dice/update investigator dice/reroll-lowest)
                                          #(items/exhaust-first-named % "Bullwhip"))))))
    game))
