(ns arkham-horror.dice)

(defn make [type]
  {:type type})

(defmulti roll :type)
(defmethod roll :random [_]
  (inc (int (rand 6))))
(defmethod roll :default [dice]
  (dice :type))

(defn accept-roll [game]
  game)

(defn roll-many [dice times]
  (take times (repeatedly #(roll dice))))

(defn combat-check [game rolls]
  (roll-many (game :dice) rolls))
