(ns arkham-horror.dice)

(defn make [value]
  {:value value})

(defmulti roll :value)
(defmethod roll :random [_]
  (inc (int (rand 6))))
(defmethod roll :default [dice]
  (dice :value))

(defn combat-check [game rolls]
  (take rolls (repeatedly #(roll (game :dice)))))

(defn accept-roll [game]
  game)

(defn save-roll [game roll]
  (assoc-in game [:dice :roll] roll))
