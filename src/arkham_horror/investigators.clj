(ns arkham-horror.investigators
  (:require [arkham-horror.investigator :as investigator]
            [arkham-horror.doom-track :as doom-track]
            [arkham-horror.ancient-one :as ancient-one]
            [arkham-horror.dice :as dice]))

(defn resolve-ancient-one-attack [{investigators :investigators
                                   :as game}]
  (assoc game :investigators (investigator/resolve-ancient-one-attack investigators)))

(defn attack
  ([game]
     (attack game (apply + (map #(dice/combat-check game (:fight %))
                                (game :investigators)))))
  ([game successes]
     (first (drop successes (iterate doom-track/retract game)))))
