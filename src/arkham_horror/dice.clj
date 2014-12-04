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

(defn pending-roll [game]
  (-> game :dice :roll))

(defn accept-roll [game]
  (update-in game [:dice] #(dissoc % :roll)))

(defn save-roll [game roll]
  (assoc-in game [:dice :roll] roll))

(defn reroll-lowest [game]
  (save-roll game (conj (rest (sort (pending-roll game)))
                        (roll (game :dice)))))
