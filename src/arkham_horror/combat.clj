(ns arkham-horror.combat
  (:require [arkham-horror.investigator :as investigator]
            [arkham-horror.ancient-one.doom-track :as doom-track]
            [arkham-horror.stat :as stat]
            [arkham-horror.investigator.dice :as dice]
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
                   :remainder 0
                   :bullwhip 0}}))

(defn end-attack [game]
  (dissoc (phase/end game) :combat))

(defn in-combat? [game]
  (game :combat))

(defn count-successes [game]
  (+ (count (filter #{5 6} (dice/pending-roll (dice/get
                                               (phase/investigator game)))))
     (or (-> game :combat :remainder) 0)))

(defn apply-successes
  ([game] (apply-successes game (count-successes game)))
  ([game successes]
     (if (or (nil? (phase/investigator game))
             (< successes (count (phase/all-investigators game))))
       (assoc-in game [:combat :remainder] successes)
       (apply-successes (ancient-one/update game (fn [investigator]
                                                   (doom-track/update investigator
                                                                      doom-track/retract)))
                        (- successes (count (phase/all-investigators game)))))))

(defn accept-roll [game]
  (phase/advance (update-in (apply-successes game) [:phase :current-investigator]
                            (fn [investigator] (dice/update investigator dice/accept-roll)))))

(defn combat-check-rolls [game fighter]
  (apply + (stat/fight fighter)
         (ancient-one/combat-modifier game)
         (map :combat-modifier (investigator/items fighter))))

(defn investigator-attack [game]
  {:pre [(:dice (phase/investigator game))]}
  (update-in game [:phase :current-investigator]
             (fn [investigator]
               (dice/update investigator
                            #(dice/combat-check
                              %
                              (phase/investigator game)
                              (apply + (ancient-one/combat-modifier (ancient-one/get game))
                                     (map :combat-modifier
                                          (investigator/items (phase/investigator game)))))))))

(defn bullwhip [game]
  (if (< (-> game :combat :bullwhip)
         (count (->> (phase/investigator game) :items
                     (filter #(= "Bullwhip" (:name %))))))
    (update-in (update-in game [:phase :current-investigator]
                          (fn [investigator]
                            (dice/update investigator dice/reroll-lowest)))
               [:combat :bullwhip] inc)
    game))
