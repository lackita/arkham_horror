(ns arkham-horror.combat
  (:refer-clojure :exclude [get])
  (:require [arkham-horror.investigator :as investigator]
            [arkham-horror.ancient-one.doom-track :as doom-track]
            [arkham-horror.stat :as stat]
            [arkham-horror.investigator.dice :as dice]
            [arkham-horror.investigator.items :as items]
            [arkham-horror.ancient-one :as ancient-one]
            [arkham-horror.phase :as phase]
            [arkham-horror.structure :as structure]
            [arkham-horror.help :as help]))

(defn update [game function]
  (update-in game [:combat] function))

(defn get [game]
  (game :combat))

(defn make []
  {:successes 0
   :remainder 0})

(defn attach-ancient-one-status-message [game]
  (help/set-message game (str "Attack\nDoom track: "
                              (-> game
                                  (structure/get-path [ancient-one doom-track])
                                  doom-track/level))))

(defn start [game]
  (let [game (assoc (phase/start game) :combat (make))]
    (attach-ancient-one-status-message game)))

(defn end [game]
  (help/set-message (dissoc (phase/end game) :combat) "Defend"))

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
      (phase/update phase/advance)
      attach-ancient-one-status-message))

(defn calculate-combat-modifier [ancient-one items]
  (apply + (ancient-one/combat-modifier ancient-one)
         (map :combat-modifier items)))

(defn investigator-attack [game]
  {:pre [(:dice (structure/get-path game [phase investigator]))]}
  (let [game (structure/update-path game [phase investigator dice]
                                    #(->> (items/get investigator)
                                          (calculate-combat-modifier (ancient-one/get game))
                                          (dice/combat-check % investigator)))]
    (help/set-message game (->> (structure/get-path game [phase investigator dice])
                                dice/pending-roll
                                (clojure.string/join " ")
                                (str "Roll: ")))))
