(ns arkham-horror.dice
  (:require [arkham-horror.phase :as phase]
            [arkham-horror.stat :as stat]
            [arkham-horror.ancient-one :as ancient-one]
            [arkham-horror.investigator :as investigator]))

(defn make [value]
  {:value value})

(defmulti roll #(-> % :dice :value))
(defmethod roll :random [_]
  (inc (int (rand 6))))
(defmethod roll :default [game]
  (-> game :dice :value))

(defn pending-roll [game]
  (-> game :dice :roll))

(defn accept-roll [game]
  (update-in game [:dice] #(dissoc % :roll)))

(defn save-roll [game roll]
  (assoc-in game [:dice :roll] roll))

(defn reroll-lowest [game]
  (save-roll game (conj (rest (sort (pending-roll game)))
                        (roll game))))

(defn skill-check [game skill modifier]
  (save-roll game (take (max 0 (+ (skill (phase/investigator game)) modifier))
                        (repeatedly #(roll game)))))

(defn combat-check [game]
  (skill-check game stat/fight
               (apply + (ancient-one/combat-modifier game)
                        (map :combat-modifier
                             (investigator/items (phase/investigator game))))))
