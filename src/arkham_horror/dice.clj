(ns arkham-horror.dice)

(defn random []
  (inc (int (rand 6))))

(defn loaded [pips]
  (fn [] pips))

(defn accept-roll [game]
  game)

(defn roll [dice times]
  (take times (repeatedly dice)))

(defn combat-check [game rolls]
  (roll (game :dice) rolls))
