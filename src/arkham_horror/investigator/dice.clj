(ns arkham-horror.investigator.dice
  (:refer-clojure :exclude [get set])
  (:require [arkham-horror.phase :as phase]
            [arkham-horror.stat :as stat]
            [arkham-horror.ancient-one :as ancient-one]))

(defn get [game]
  (game :dice))

(defn set [game dice]
  (assoc game :dice dice))

(defn update [g f]
  (update-in g [:dice] f))

(defn make [value]
  {:value (or value :random)})

(defmulti roll :value)
(defmethod roll :random [_]
  (inc (int (rand 6))))
(defmethod roll :default [dice]
  (dice :value))

(defn pending-roll [dice]
  (dice :roll))

(defn accept-roll [dice]
  (dissoc dice :roll))

(defn save-roll [dice roll]
  (assoc dice :roll roll))

(defn reroll-lowest [dice]
  (save-roll dice (conj (rest (sort (pending-roll dice)))
                        (roll dice))))

(defn skill-check [dice investigator skill modifier]
  (save-roll dice (take (max 0 (+ (skill investigator) modifier))
                        (repeatedly #(roll dice)))))

(defn combat-check [dice investigator modifier]
  (skill-check dice investigator stat/fight modifier))
