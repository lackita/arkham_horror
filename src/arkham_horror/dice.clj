(ns arkham-horror.dice)

(defn random []
  (inc (int (rand 6))))

(defn loaded [pips]
  (fn [] pips))

(defn roll [dice times]
  (take times (repeatedly dice)))

(defn combat-check [game rolls]
  (count (filter #(#{5 6} %) (roll (game :dice) rolls))))
